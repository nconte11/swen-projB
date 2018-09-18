package automail;

import exceptions.FragileItemBrokenException;
import exceptions.ItemTooHeavyException;

public class CarefulRobot extends Robot {
	private final static int MAX_CAPACITY = 3;
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
