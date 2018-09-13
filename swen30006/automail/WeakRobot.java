package automail;


public class WeakRobot extends Robot {
	
	private int MAX_CAPACITY = 4;
	

	public WeakRobot() {
		super();
		super.setStrong(false);
		super.setCareful(false);
		// TODO Auto-generated constructor stub
		tube = new StorageTube(this.MAX_CAPACITY);
	}
	
	

}
