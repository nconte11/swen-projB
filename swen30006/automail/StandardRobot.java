package automail;

public class StandardRobot extends Robot {
	
	private int MAX_CAPACITY = 4;

	public StandardRobot() {
		super();
		super.setStrong(true);
		super.setCareful(false);
		// TODO Auto-generated constructor stub
		tube = new StorageTube(this.MAX_CAPACITY);
	}

}
