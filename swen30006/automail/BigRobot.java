package automail;

import strategies.IMailPool;

public class BigRobot extends Robot {
	
	private int MAX_CAPACITY = 6;

	public BigRobot(IMailDelivery delivery, IMailPool mailPool, boolean strong, boolean careful) {
		super(delivery, mailPool, strong, careful);
		super.tube = new StorageTube(this.MAX_CAPACITY);
	}
	
	

}
