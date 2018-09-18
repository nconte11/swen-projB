package automail;

// Robot that can carry extra mail
public class BigRobot extends Robot {
	
	private final static int MAX_CAPACITY = 6;

	public BigRobot() {
		super();
		super.maxCapacity = BigRobot.MAX_CAPACITY;
	}
}
