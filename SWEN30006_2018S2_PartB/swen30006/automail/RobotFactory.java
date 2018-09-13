package automail;

public class RobotFactory {
	public Robot createRobot(String robotType) {
		if (robotType == null) return null;
		
		if (robotType.equalsIgnoreCase("weak")) return new weakRobot();
		else if (robotType.equalsIgnoreCase("standard")) return new standardRobot();
		else if (robotType.equalsIgnoreCase("big")) return new bigRobot();
		else if (robotType.equalsIgnoreCase("careful")) return new carefulRobot();
		
		return null;
	}
}
