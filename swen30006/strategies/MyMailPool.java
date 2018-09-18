package strategies;

import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.ListIterator;
import java.util.function.Consumer;

import automail.MailItem;
import automail.PriorityMailItem;
import automail.Robot;
import automail.StorageTube;
import exceptions.TubeFullException;
//import strategies.MyMailPool.Item;
import exceptions.FragileItemBrokenException;

public class MyMailPool implements IMailPool {
//	private class Item {
//		int priority;
//		int destination;
//		boolean heavy;
//		boolean fragile;
//		MailItem mailItem;
//		// Use stable sort to keep arrival time relative positions
//		
//		public Item(MailItem mailItem) {
//			priority = (mailItem instanceof PriorityMailItem) ? ((PriorityMailItem) mailItem).getPriorityLevel() : 1;
//			heavy = mailItem.getWeight() >= 2000;
//			destination = mailItem.getDestFloor();
//			this.mailItem = mailItem;
//			this.fragile = mailItem.getFragile();
//		}
//	}
	
//	public class ItemComparator implements Comparator<Item> {
//		@Override
//		public int compare(Item i1, Item i2) {
//			int order = 0;
//			if (i1.priority < i2.priority) {
//				order = 1;
//			} else if (i1.priority > i2.priority) {
//				order = -1;
//			} else if (i1.destination < i2.destination) {
//				order = 1;
//			} else if (i1.destination > i2.destination) {
//				order = -1;
//			}
//			return order;
//		}
//	}
	
//	private LinkedList<Item> pool;
	private int MAX_TAKE;
//	private LinkedList<Robot> robots;
//	private int lightCount;
	
	
	private ArrayList<Robot> allRobots;
	private ArrayList<MailItem> items;

	public MyMailPool(){
		// Start empty
//		pool = new LinkedList<Item>();
//		lightCount = 0;
//		robots = new LinkedList<Robot>();
		allRobots = new ArrayList<Robot>();
		items = new ArrayList<MailItem>();
		
	}

	public void addToPool(MailItem mailItem) {
		items.add(mailItem);
		
//		Item item = new Item(mailItem);
//		
//		pool.add(item);
//		if (!item.heavy) lightCount++;
//		pool.sort(new ItemComparator());
	}
	
	@Override
	public void step() throws FragileItemBrokenException {
		for (Robot r: allRobots) {
			fillStorageTube(r);
		}
	}
	
	private void fillStorageTube(Robot robot) throws FragileItemBrokenException {
		StorageTube tube = robot.getTube();
		this.MAX_TAKE = robot.getTube().MAXIMUM_CAPACITY;
		
		StorageTube temp = new StorageTube(MAX_TAKE);
		
		try {

			for (MailItem i : items) {
				if (temp.isFull()) break;
				
				if (i.getFragile()) {
					if (robot.isCareful() && !robot.getHasFragile()) {
						temp.addItem(i);
						robot.setHasFragile(true);
					}
				}
				
				else if (i.getWeight() >= 2000) {
					if (!robot.isWeak()) {
						temp.addItem(i);
					}
				}
				
				else {
					temp.addItem(i);
				}
			}
			
			if (!temp.isEmpty()) {
				while (!temp.isEmpty()) {
					MailItem item = temp.pop();
					tube.addItem(item);
					items.remove(item);
				}
				
				
			}
			
			if (!tube.isEmpty()) {
				robot.dispatch();
			}
			
//			else {
//				System.out.println(items.toString());
//			}
		}
		
		catch (TubeFullException e) {
			e.printStackTrace();
		}
		
		
		
//		StorageTube tube = robot.getTube();
//		this.MAX_TAKE = robot.getTube().MAXIMUM_CAPACITY;
//		
//		// temp storagetube now has the same max capacity as the robot
//		StorageTube temp = new StorageTube(robot.getTube().MAXIMUM_CAPACITY);
//		
//		try { 
//			// check if it's a careful robot, go through the pool to find a fragile item		
//			if (robot.isCareful()) {
//				ListIterator<Item> i = pool.listIterator();
//				while (tube.isEmpty() && i.hasNext()) {
//					Item item = i.next();
//					if (item.fragile) {
//						temp.addItem(item.mailItem);
//						if (!item.heavy) lightCount--;
//						i.remove();
//					}
//				}
//			}
//			
//			// if the tube isn't empty, it's a careful robot holding a fragile item
//			if (tube.isEmpty()) {
//				
//				if (robot.isStrong()) {
//					ListIterator<Item> i = pool.listIterator();
//					// if tube isn't full, and the robot hasn't gone through the whole list
//					while(temp.getSize() < MAX_TAKE && i.hasNext() ) {
//						Item item = i.next();
//						if (!item.fragile) {
//							temp.addItem(item.mailItem);
//							if (!item.heavy) lightCount--;
//							i.remove();
//						}
//					}
//				} else {
//
//					ListIterator<Item> i = pool.listIterator();
//					
//					// stop when they tube is full, 
//					while(temp.getSize() < MAX_TAKE && lightCount > 0 && i.hasNext()) {
//						Item item = i.next();
//
//						if (!item.heavy && !item.fragile) {
//							temp.addItem(item.mailItem);
//							i.remove();
//							lightCount--;
//						}
//					}
//				}
//				if (temp.getSize() > 0) {
//					while (!temp.isEmpty()) tube.addItem(temp.pop());
//					robot.dispatch();
//				}
//			}
//		}
//		catch(TubeFullException e){
//			e.printStackTrace();
//		}
	}
	
	@Override
	public void registerWaiting(Robot robot) {
		allRobots.add(robot);
	}

	@Override
	public void deregisterWaiting(Robot robot) {
		allRobots.remove(robot);
	}

}
