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
			fragile = mailItem.getFragile();
			destination = mailItem.getDestFloor();
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
	private LinkedList<Item> fragilePool;
	private LinkedList<Robot> robots;
	private int lightCount;
	private int fragileCount;

	public MyMailPool(){
		// Start empty
		pool = new LinkedList<Item>();
		fragilePool = new LinkedList<Item>();
		lightCount = 0;
		fragileCount = 0;
		robots = new LinkedList<Robot>();
	}

	public void addToPool(MailItem mailItem) {
		Item item = new Item(mailItem);
		if (item.fragile) {
			fragilePool.add(item);
			fragilePool.sort(new ItemComparator());
		}
		
		else {
			pool.add(item);
			if (!item.heavy) lightCount++;
			pool.sort(new ItemComparator());
		}
	}
	
	@Override
	public void step() throws FragileItemBrokenException {
		for (Robot robot: (Iterable<Robot>) robots::iterator) { fillStorageTube(robot); }
	}
	
	private void fillStorageTube(Robot robot) throws FragileItemBrokenException {
		StorageTube tube = robot.getTube();
		StorageTube temp = new StorageTube(tube.MAXIMUM_CAPACITY);
		try { // Get as many items as available or as fit
				if (robot.isCareful()) {
					if (!fragilePool.isEmpty()) {
						Item item = fragilePool.remove();
						temp.addItem(item.mailItem);
					}
				}
			
				else if (!robot.isWeak()) {
					while(temp.getSize() < temp.MAXIMUM_CAPACITY && !pool.isEmpty() ) {
						if (!pool.peek().fragile) {
							Item item = pool.remove();
							if (!item.heavy) lightCount--;
							temp.addItem(item.mailItem);
						}
						
					}
				} else {
					ListIterator<Item> i = pool.listIterator();
					while(temp.getSize() < temp.MAXIMUM_CAPACITY && lightCount > 0) {
						Item item = i.next();
						if (item.fragile) continue;
						else if (!item.heavy) {
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
		if (!robot.isWeak()) {
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
