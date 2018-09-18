package strategies;

import java.util.ArrayList;
import automail.Building;
import automail.MailItem;
import automail.PriorityMailItem;
import automail.Robot;
import automail.StorageTube;
import exceptions.TubeFullException;
import exceptions.FragileItemBrokenException;

public class MyMailPool implements IMailPool {
	private int MAX_TAKE;	
	
	private ArrayList<Robot> allRobots;
	
	// one pool for each type of mail
	private ArrayList<MailItem> items;
	private ArrayList<MailItem> priorityItems;

	public MyMailPool(){
		allRobots = new ArrayList<Robot>();
		items = new ArrayList<MailItem>();
		priorityItems = new ArrayList<MailItem>();
		
	}

	public void addToPool(MailItem mailItem) {
		// Add priority mail items into the relevant array
		if (mailItem instanceof PriorityMailItem) {
			priorityItems.add(mailItem);
		}
		
		// Add items on the base floor to the priority 
		// array due to the ease of delivery
		else if (mailItem.getDestFloor() == Building.MAILROOM_LOCATION) {
			priorityItems.add(0, mailItem);
		}
		
		// Put standard items into the items array and
		else {
			items.add(mailItem);
			items.sort((a,b) -> (int) Math.ceil((scoreEstimate(b) - scoreEstimate(a)))); 
		}
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
		
		ArrayList<MailItem> temp = new ArrayList<MailItem>(MAX_TAKE);
		Boolean priority = false;
		
		try {
			
			for (MailItem i : priorityItems) {
				if (temp.size() == MAX_TAKE) break;
				
				if (i.getFragile()) {
					/* only a careful robot that doesn't already have fragile mail
					can pick up fragile mail */
					if (robot.isCareful() && !robot.getHasFragile()) {
						temp.add(i);
						priority = true;
						robot.setHasFragile(true);
					}
				}
				
				// only strong robots can pick up heavy mail
				else if (i.getWeight() >= 2000) {
					if (!robot.isWeak()) {
						temp.add(i);
						priority = true;
					}
				}
				
				else {
					temp.add(i);
					priority = true;
				}
				
			}
			
			
			if (priority || (!items.isEmpty() && scoreEstimate(items.get(0)) > 30)) {
				for (MailItem i : items) {
					if (temp.size() == MAX_TAKE) break;
					
					if (i.getFragile()) {
						if (robot.isCareful() && !robot.getHasFragile()) {
							temp.add(i);
							robot.setHasFragile(true);
						}
					}
					
					else if (i.getWeight() >= 2000) {
						if (!robot.isWeak()) {
							temp.add(i);
						}
					}
					
					else {
						temp.add(i);
					}
				}
			}
			
			// make sure the robots aren't constantly travelling up and down the floors
			temp.sort((a, b) -> b.getDestFloor() - a.getDestFloor());
			
			// put the mail in the tube and remove them from the pools
			if (!temp.isEmpty()) {
				while (!temp.isEmpty()) {
					MailItem item = temp.remove(0);
					tube.addItem(item);
					if (items.indexOf(item) != -1) items.remove(item);
					else priorityItems.remove(item);
				}
			}
			
			if (!tube.isEmpty()) {
				robot.dispatch();
			}
		}
		
		catch (TubeFullException e) {
			e.printStackTrace();
		}
	}
	
	// A function used to estimate the projected score
	// of an item for prioritisation
	private double scoreEstimate(MailItem i) {
		int destination = i.getDestFloor();
		int arrivalTime = i.getArrivalTime();
		int priority = 0;
		
		if (i instanceof PriorityMailItem) {
			priority = ((PriorityMailItem) i).getPriorityLevel();
		}
		
		// Applying the same formula, extra + 1 is to compensate
		// for the extra time of actual item delivery
		return Math.pow(destination + 1 + automail.Clock.Time() - arrivalTime, 1.2) * (1 + Math.sqrt(priority));
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
