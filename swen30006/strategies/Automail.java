package strategies;

import automail.IMailDelivery;
import automail.Robot;
import automail.RobotFactory;

public class Automail {
	      
    public Robot[] robot;
    public IMailPool mailPool;
    private RobotFactory robotFactory;
    
    public Automail(IMailPool mailPool, IMailDelivery delivery, String robotString) {
	    	// Swap between simple provided strategies and your strategies here
	    	    	
	    	/** Initialize the MailPool */
	    	
	    	this.mailPool = mailPool;
	    	this.robotFactory = new RobotFactory();
	    	
	    	String[] robots = robotString.split(",");
	    	
	    	robot = new Robot[robots.length];
	    	
	    	for (int i = 0; i < robots.length; i++) {
	    		robot[i] = robotFactory.createRobot(robots[i]);
	    		robot[i].setup(delivery, this.mailPool);
	    	}
    }
    
}
