package strategies;

import automail.BigRobot;
import automail.CarefulRobot;
import automail.IMailDelivery;
import automail.Robot;
import automail.StandardRobot;
import automail.WeakRobot;

public class Automail {
	      
    public Robot[] robot;
    public IMailPool mailPool;
    
    public Automail(IMailPool mailPool, IMailDelivery delivery) {
    	// Swap between simple provided strategies and your strategies here
    	    	
    	/** Initialize the MailPool */
    	
    	this.mailPool = mailPool;
    	
        /** Initialize the RobotAction */
    	boolean weak = false;  // Can't handle more than 2000 grams
    	boolean strong = true; // Can handle any weight that arrives at the building
    	boolean careful = true;
    	boolean notCareful = false;
    	
    	/** Initialize robots */
    	robot = new Robot[4];
    	robot[0] = new WeakRobot(delivery, mailPool, weak, notCareful);
    	robot[1] = new StandardRobot(delivery, mailPool, strong, notCareful);
    	robot[2] = new BigRobot(delivery, mailPool, strong, notCareful);
    	robot[3] = new CarefulRobot(delivery, mailPool, strong, careful);
    }
    
}
