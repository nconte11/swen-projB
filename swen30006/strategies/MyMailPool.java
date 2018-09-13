package strategies;

import java.util.LinkedList;
import java.util.Comparator;
import java.util.ListIterator;
import java.util.function.Consumer;

import automail.MailItem;
import automail.PriorityMailItem;
import automail.Robot;
import automail.StorageTube;
import exceptions.TubeFullException;
import exceptions.FragileItemBrokenException;

public class MyMailPool implements IMailPool {
	private class Item {
		int priority;
		int destination;
		boolean heavy;
		boolean fragile;
		MailItem mailItem;
		// Use stable sort to keep arrival time relative positions
		
		public Item(MailItem mailItem) {
			priority = (mailItem instanceof PriorityMailItem) ? ((PriorityMailItem) mailItem).getPriorityLevel() : 1;
			heavy = mailItem.getWeight() >= 2000;
			destination = mailItem.getDestFloor();
			fragile = mailItem.getFragile();
			this.mailItem = mailItem;
		}

	}
	
	public class ItemComparator implements Comparator<Item> {
		@Override
		public int compare(Item i1, Item i2) {
			int order = 0;
			if (i1.priority < i2.priority) {
				order = 1;
			} else if (i1.priority > i2.priority) {
				order = -1;
			} else if (i1.destination < i2.destination) {
				order = 1;
			} else if (i1.destination > i2.destination) {
				order = -1;
			}
			return order;
		}
	}
	
	private LinkedList<Item> pool;
	
	// no longer static final, depends on robot
	private int MAX_TAKE;
	private LinkedList<Robot> robots;
	private int lightCount;

	public MyMailPool(){
		// Start empty
		pool = new LinkedList<Item>();
		lightCount = 0;
		robots = new LinkedList<Robot>();
	}

	public void addToPool(MailItem mailItem) {
		Item item = new Item(mailItem);
		pool.add(item);
		if (!item.heavy) lightCount++;
		pool.sort(new ItemComparator());
	}
	
	@Override
	public void step() throws FragileItemBrokenException {
		for (Robot robot: (Iterable<Robot>) robots::iterator) { fillStorageTube(robot); }
	}
	
	private void fillStorageTube(Robot robot) throws FragileItemBrokenException {
		StorageTube tube = robot.getTube();
		this.MAX_TAKE = robot.getTube().MAXIMUM_CAPACITY;
		
		// temp storagetube now has the same max capacity as the robot
		StorageTube temp = new StorageTube(robot.getTube().MAXIMUM_CAPACITY);
		
		try { // check if it's a careful robot, if it is and there are fragile mail items, pick one
			if (robot.isCareful()) {
				ListIterator<Item> i = pool.listIterator();
				while (i != null) {
					Item item = pool.peek();
					if (item.fragile) {
						temp.addItem(item.mailItem);
						
						break;
					}
					i.next();
				}
				
			} else if (robot.isStrong()) {
					while(temp.getSize() < MAX_TAKE && !pool.isEmpty() ) {
						
						// make sure it isn't 'fragile' mail
						Item item = pool.remove();
						if (item.fragile) {
							pool.add(pool.size(), item);
						} else if (!item.heavy) lightCount--;
							temp.addItem(item.mailItem);
					}
				} else {
					
					// make sure it isn't 'fragile'
					
					ListIterator<Item> i = pool.listIterator();
					
					while(temp.getSize() < MAX_TAKE && lightCount > 0) {
						Item item = i.next();
						if (!item.heavy && !item.fragile) {
							temp.addItem(item.mailItem);
							i.remove();
							lightCount--;
						}
					}
				}
				if (temp.getSize() > 0) {
					while (!temp.isEmpty()) tube.addItem(temp.pop());
					robot.dispatch();
				}
		}
		catch(TubeFullException e){
			e.printStackTrace();
		}
	}

	@Override
	public void registerWaiting(Robot robot) { // assumes won't be there
		if (robot.isStrong()) {
			robots.add(robot); 
		} else {
			robots.addLast(robot); // weak robot last as want more efficient delivery with highest priorities
		}
	}

	@Override
	public void deregisterWaiting(Robot robot) {
		robots.remove(robot);
	}

}
