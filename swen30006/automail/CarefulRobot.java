package automail;

import exceptions.FragileItemBrokenException;
import exceptions.ItemTooHeavyException;

// Robot that can carry fragile mail because it's slow and carries less
public class CarefulRobot extends Robot {
	private final static int MAX_CAPACITY = 3;
	
	// Counter used to track the slow movement
	private int counter = 0;

	public CarefulRobot() {
		super();
		super.careful = true;
		super.maxCapacity = CarefulRobot.MAX_CAPACITY;
		
	}
	
	@Override
	public void moveTowards(int destination) throws FragileItemBrokenException, ItemTooHeavyException {
		
		if (counter == 1) {
			super.moveTowards(destination);
		}
		
		// Restricts counter to 0 or 1
        counter = (counter + 1) % 2;
    }

}
