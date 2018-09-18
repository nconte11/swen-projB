package automail;

import strategies.IMailPool;

public class StandardRobot extends Robot {
	
	private int MAX_CAPACITY = 4;

	public StandardRobot(IMailDelivery delivery, IMailPool mailPool) {
		super(delivery, mailPool);
		super.tube = new StorageTube(this.MAX_CAPACITY);
	}

}
