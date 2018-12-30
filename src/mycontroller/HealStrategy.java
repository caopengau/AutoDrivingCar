package mycontroller;

import utilities.Coordinate;
import world.Car;
import tiles.MapTile;
import tiles.TrapTile;

public class HealStrategy implements Strategy{
	
	private Car car;
	private CarSensor cs;
	private CarMovement cm;
	private float MAX_HEALTH;
	
	
	private Coordinate healingPos;
	

	public HealStrategy(Car car, CarSensor cs, CarMovement cm) {
		super();
		this.car = car;
		this.cs = cs;
		this.cm = cm;
		
		MAX_HEALTH = car.getHealth(); 
		
		healingPos = null;
		
	}

	@Override
	public void getMoving(float delta) {
		if(healingPos==null)
			healingPos = cs.checkTileTypeAhead(car.getOrientation(), car.getView(), 1, MapTile.Type.TRAP);
		
		if(car.getHealth()==MAX_HEALTH) {
			healingPos = null;
		}
		
		if(healingPos!=null) {
			TrapTile tile;
			tile = (TrapTile) car.getView().get(healingPos);
			if(tile!=null && tile.getTrap().equals("health")) {
				System.out.println("healing................................");
				moveTowards(healingPos);
			}
			
			if(car.getHealth()==MAX_HEALTH) {
				healingPos = null;
			}
		}
		
		
		
		
	}

	public void moveTowards(Coordinate target) {
		Coordinate currPos = new Coordinate(car.getPosition());
		
		switch (car.getOrientation()) {
		case EAST:
			if(currPos.x < target.x) {
				car.applyForwardAcceleration();
			} else if(currPos.x > target.x) {
				car.applyReverseAcceleration();
			}
			break;
		case WEST:
			if(currPos.x > target.x) {
				car.applyForwardAcceleration();
			} else if(currPos.x < target.x) {
				car.applyReverseAcceleration();
			}
			break;
		case NORTH:
			if(currPos.y < target.y) {
				car.applyForwardAcceleration();
			} else if(currPos.x > target.x) {
				car.applyReverseAcceleration();
			}
			break;
		case SOUTH:
			if(currPos.y > target.y) {
				car.applyForwardAcceleration();
			} else if(currPos.x < target.x) {
				car.applyReverseAcceleration();
			}
			break;
		}
	}
	
	@Override
	public String getStrategyName() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
