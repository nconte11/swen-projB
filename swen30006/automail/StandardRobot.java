package automail;

import strategies.IMailPool;

public class StandardRobot extends Robot {
	
	private int MAX_CAPACITY = 4;

	public StandardRobot() {
		super();
		super.tube = new StorageTube(this.MAX_CAPACITY);
	}

}
