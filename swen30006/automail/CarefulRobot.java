package automail;

import exceptions.FragileItemBrokenException;

public class CarefulRobot extends Robot {
	
	private int MAX_CAPACITY = 3;

	public CarefulRobot() {
		super();
		super.setStrong(true);
		super.setCareful(true);
		// TODO Auto-generated constructor stub
		tube = new StorageTube(this.MAX_CAPACITY);
		
	}
	
	@Override
	public void moveTowards(int destination) throws FragileItemBrokenException {
		int counter = 0;
		if (counter % 2 == 1) {
			if (getDeliveryItem() != null && getDeliveryItem().getFragile() || !tube.isEmpty() && tube.peek().getFragile()) throw new FragileItemBrokenException();
        	if(getCurrent_floor() < destination){
            	setCurrent_floor(getCurrent_floor()+1);
        	}
        	else{
            	setCurrent_floor(getCurrent_floor()-1);
        	}
		}
        counter++;
    }

}
