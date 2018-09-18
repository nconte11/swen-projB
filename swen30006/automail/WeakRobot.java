package automail;

import strategies.IMailPool;

public class WeakRobot extends Robot {
	
	private int MAX_CAPACITY = 4;
	

	public WeakRobot() {
		super();
		super.tube = new StorageTube(this.MAX_CAPACITY);
		super.weak = true;
	}
	
	

}
