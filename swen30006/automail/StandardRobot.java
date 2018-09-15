package automail;

import strategies.IMailPool;

public class StandardRobot extends Robot {
	
	private int MAX_CAPACITY = 4;

	public StandardRobot(IMailDelivery delivery, IMailPool mailPool, boolean strong, boolean careful) {
		super(delivery, mailPool, strong, careful);
		super.tube = new StorageTube(this.MAX_CAPACITY);
	}

}
