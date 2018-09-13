package automail;

public class BigRobot extends Robot {
	
	private int MAX_CAPACITY = 6;

	public BigRobot() {
		super();
		super.setStrong(true);
		super.setCareful(false);
		// TODO Auto-generated constructor stub
		
		tube = new StorageTube(this.MAX_CAPACITY);
	}
	
	

}
