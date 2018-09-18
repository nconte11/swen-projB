package automail;
// Class that creates the Robot types specified by Automail.properties

public class RobotFactory {
	public Robot createRobot(String robotType) {
		if (robotType == null) return null;
		
		if (robotType.equalsIgnoreCase("weak")) return new WeakRobot();
		else if (robotType.equalsIgnoreCase("standard")) return new StandardRobot();
		else if (robotType.equalsIgnoreCase("big")) return new BigRobot();
		else if (robotType.equalsIgnoreCase("careful")) return new CarefulRobot();
		
		return null;
	}
}
