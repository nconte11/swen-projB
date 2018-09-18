package automail;

import exceptions.FragileItemBrokenException;
import exceptions.ItemTooHeavyException;
import strategies.IMailPool;

public class CarefulRobot extends Robot {
	
	private int MAX_CAPACITY = 3;
	private int counter = 0;

	public CarefulRobot() {
		super();
		super.tube = new StorageTube(this.MAX_CAPACITY);
		super.careful = true;
		
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
