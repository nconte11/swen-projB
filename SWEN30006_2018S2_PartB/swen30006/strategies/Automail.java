package strategies;

import automail.IMailDelivery;
import automail.Robot;
import automail.RobotFactory;

public class Automail {
	      
    public Robot[] robotList;
    public IMailPool mailPool;
    
	RobotFactory robotFactory = new RobotFactory();
    
    public Automail(IMailPool mailPool, IMailDelivery delivery, String robotInput) {
    
	    	/** Initialize mail pool **/
	    	this.mailPool = mailPool;
	    	
	    	String[] robotTypes = robotInput.split(",");
	    	
	    	robotList = new Robot[robotTypes.length];
	    	
	    	for (int i = 0; i < robotTypes.length; i++) {
	    		Robot newRobot = robotFactory.createRobot(robotTypes[i]);
	    		
	    		newRobot.setDelivery(delivery);
	    		newRobot.setMailPool(mailPool);
	    		
	    		robotList[i] = newRobot;
	    	}
    }
    
}
