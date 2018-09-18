package automail;

import exceptions.ExcessiveDeliveryException;
import exceptions.ItemTooHeavyException;
import exceptions.FragileItemBrokenException;
import strategies.IMailPool;
import java.util.Map;
import java.util.TreeMap;

/**
 * The robot delivers mail!
 */
public abstract class Robot {

	protected StorageTube tube;
	
    protected IMailDelivery delivery;
    protected final String id;
    /** Possible states the robot can be in */
    public enum RobotState { DELIVERING, WAITING, RETURNING }
    public RobotState current_state;
    private int current_floor;
    private int destination_floor;
    private IMailPool mailPool;
    private boolean receivedDispatch;
	private MailItem deliveryItem;
	private int deliveryCounter;

	protected int maxCapacity = 4;
	
    protected boolean careful = false;
    protected boolean weak = false;
    // used to check if a CarefulRobot is already carrying fragile mail
    protected boolean hasFragile = false;
    
    /*
     * 
     * GETTERS
     * 
     */
    
    public boolean isCareful()			{ return this.careful; }
    public boolean isWeak()				{ return this.weak; }
    public int getCurrent_floor() 		{ return current_floor; }
    public MailItem getDeliveryItem() 	{ return deliveryItem; }
	public StorageTube getTube()			{ return tube; }
	public boolean getHasFragile()		{ return hasFragile; }
    
    
    /*
     * 
     * SETTERS
     * 
     */
    
    public void setCurrent_floor(int current_floor)		{ this.current_floor = current_floor; }
    public void setDeliveryItem(MailItem deliveryItem)	{ this.deliveryItem = deliveryItem; }
    public void createTube()								{ tube = new StorageTube(maxCapacity); }
    public void dispatch() 								{ receivedDispatch = true; }
    public void setHasFragile(boolean hasFragile)		{ this.hasFragile = hasFragile; }

    /**
     * Initiates the robot's location at the start to be at the mailroom
     * also set it to be waiting for mail.
     * @param behaviour governs selection of mail items for delivery and behaviour on priority arrivals
     * @param delivery governs the final delivery
     * @param mailPool is the source of mail items
     * @param strong is whether the robot can carry heavy items
     */
    public Robot(){
    	id = "R" + hashCode();
    	current_state = RobotState.RETURNING;
        current_floor = Building.MAILROOM_LOCATION;
        this.receivedDispatch = false;
        this.deliveryCounter = 0;
    }
    
    
    
    public void setup(IMailDelivery delivery, IMailPool mailPool) {
        this.delivery = delivery;
        this.mailPool = mailPool;
        createTube();
    }
    
    /**
     * This is called on every time step
     * @throws ExcessiveDeliveryException if robot delivers more than the capacity of the tube without refilling
     */
    public void step() throws ExcessiveDeliveryException, ItemTooHeavyException, FragileItemBrokenException {    
    		switch (current_state) {
    			/** This state is triggered when the robot is returning to the mailroom after a delivery */
    			case RETURNING:
    				
    				/** If its current position is at the mailroom, then the robot should change state */
    				if (current_floor == Building.MAILROOM_LOCATION) {
    					
    					// CarefulRobot is no longer carrying a fragile item
    					this.hasFragile = false;
    					
    					/** Return any items to the pool that it is still carrying */
    					while (!tube.isEmpty()) {
    						MailItem mailItem = tube.pop();
    						mailPool.addToPool(mailItem);
    						
    						System.out.printf("T: %3d > old addToPool [%s]%n", Clock.Time(), mailItem.toString());
    					}
    					
    					mailPool.registerWaiting(this);
    					changeState(RobotState.WAITING);
    				}
    				
    				/** If the robot is not at the mailroom floor yet, then move towards it */
    				else {
    					moveTowards(Building.MAILROOM_LOCATION);
    				}
    				
    				break;
    			
    			case WAITING:
    				/** If the StorageTube is ready and the Robot has been dispatched then start the delivery */
    				if (!tube.isEmpty() && receivedDispatch) {
    					receivedDispatch = false;
    					deliveryCounter = 0;
    					setRoute();
    					mailPool.deregisterWaiting(this);
    					changeState(RobotState.DELIVERING);
    				}
    				
    				break;
    			
    			case DELIVERING:
    				if (current_floor == destination_floor) {
    					delivery.deliver(deliveryItem);
    					deliveryCounter++;
    					
    					if (deliveryCounter > this.tube.MAXIMUM_CAPACITY) {
    						throw new ExcessiveDeliveryException();
    						}
    						
    					/** Return if there are no more items in the tube */
    					if (tube.isEmpty()) {
    						changeState(RobotState.RETURNING);
    					}
    					
    					/** If there are more items, set the robot's route to the location to deliver the item */
    					else {
    						setRoute();
        					changeState(RobotState.DELIVERING);
    					}
    					
    				}
    				
    				/** The robot is not at the destination yet, so move towards it */
    				else {
    					moveTowards(destination_floor);
    				}
    				break;
    		}
    }

    /**
     * Sets the route for the robot
     */
    private void setRoute() throws ItemTooHeavyException, FragileItemBrokenException{
        deliveryItem = tube.pop();
        
        // Throw error if this robot cannot carry the item
        if (isWeak() && deliveryItem.weight > 2000) throw new ItemTooHeavyException(); 
        if (!isCareful() && deliveryItem.fragile) throw new FragileItemBrokenException();
        
        destination_floor = deliveryItem.getDestFloor();
    }

    /**
     * Generic function that moves the robot towards the destination
     * @param destination the floor towards which the robot is moving
     */
    public void moveTowards(int destination) throws FragileItemBrokenException, ItemTooHeavyException {
    		if (isWeak() && deliveryItem.weight > 2000) throw new ItemTooHeavyException(); 
        if (!isCareful() && deliveryItem.fragile) throw new FragileItemBrokenException();
    	
        current_floor = (current_floor < destination) ? current_floor + 1 : current_floor - 1;
    }
    
    private String getIdTube() {
		return String.format("%s(%1d/%1d)", id, tube.getSize(), tube.MAXIMUM_CAPACITY);
    }
    
    /**
     * Prints out the change in state
     * @param nextState the state to which the robot is transitioning
     */
    private void changeState(RobotState nextState){
    	if (current_state != nextState) {
            System.out.printf("T: %3d > %7s changed from %s to %s%n", Clock.Time(), getIdTube(), current_state, nextState);
    	}
    	current_state = nextState;
    	if(nextState == RobotState.DELIVERING){
            System.out.printf("T: %3d > %7s-> [%s]%n", Clock.Time(), getIdTube(), deliveryItem.toString());
    	}
    }
    
    /*
     * 
     * HASH CODE
     * 
     */
    
	static private int count = 0;
	static private Map<Integer, Integer> hashMap = new TreeMap<Integer, Integer>();

	@Override
	public int hashCode() {
		Integer hash0 = super.hashCode();
		Integer hash = hashMap.get(hash0);
		if (hash == null) { hash = count++; hashMap.put(hash0, hash); }
		return hash;
	}
}
