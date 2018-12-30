package mycontroller;

import tiles.MapTile;
import utilities.Coordinate;
import world.Car;
import world.WorldSpatial;

public class CarMovement {
	private Car car;
	private CarSensor cs;
	private WorldSpatial.Direction curDir = null;
	

	private int fastenBeforeNtileBeforeLava = 1;
	
	
	public CarMovement(Car car, CarSensor cs) {
		super();
		this.car = car;
		this.cs = cs;
		
	}
	
	public boolean getPassLavaFast() {
		Coordinate coord = new Coordinate(car.getPosition());
		MapTile tile = car.getView().get(coord);
		MapTile tileFront = null;
		
		Coordinate coordFront = cs.checkTileTypeAhead(car.getOrientation(), car.getView(), fastenBeforeNtileBeforeLava, MapTile.Type.TRAP);
		if(coordFront!=null)
			tileFront = car.getView().get(coordFront);
		
		if(cs.isLavaTile(tile)||cs.isLavaTile(tileFront)) {
			System.out.println("lava increases speed");
			car.applyForwardAcceleration();
			car.applyForwardAcceleration();
			car.applyForwardAcceleration();
			return true;
		}
		return false;
	}
	
	
	/**
	 * this function adjust the miss-aligned car until it hit the closest direction, one of 
	 * {EAST, WEST, SOUTH, NORTH}
	 * @param delta
	 * @return true for adjusting direction completed
	 * 			false for still need more adjusting
	 */
	public boolean adjust(float delta) {
		float result = car.getAngle()%90;
		if(result == 0)  return true;
		
		if(car.getSpeed()==0) car.applyForwardAcceleration();
		
		if(result < 45) car.turnRight(delta);
		else car.turnLeft(delta);
		return false;
	}
	
	/**
	 * this function turns the car until it hit a new direction, one of {EAST, WEST, SOUTH, NORTH}
	 * @param dir turn left or right?
	 * @param delta small directional change per unit time;
	 * @return true for completing the turn
	 * 			false for still turning
	 */
	
	public boolean turnDir(WorldSpatial.RelativeDirection dir, float delta) {

		if(car.getSpeed()==0) {
			car.applyForwardAcceleration();
		}
		if(curDir==null) {
			curDir = car.getOrientation();
		}
		
		if(curDir!=car.getOrientation()) {
			curDir = null;
			return true;
		}
		
		switch (dir){
		case LEFT:
			car.turnLeft(delta);
			break;
		case RIGHT:
			car.turnRight(delta);
			break;
		}
		return false;
			
	}
	
	/**
	 * Turn the car clock wise (think of a compass going clock-wise)
	 */
	public void applyRightTurn(WorldSpatial.Direction orientation, float delta) {
		switch(orientation){
		case EAST:
			if(!car.getOrientation().equals(WorldSpatial.Direction.SOUTH)){
				car.turnRight(delta);
			}
			break;
		case NORTH:
			if(!car.getOrientation().equals(WorldSpatial.Direction.EAST)){
				car.turnRight(delta);
			}
			break;
		case SOUTH:
			if(!car.getOrientation().equals(WorldSpatial.Direction.WEST)){
				car.turnRight(delta);
			}
			break;
		case WEST:
			if(!car.getOrientation().equals(WorldSpatial.Direction.NORTH)){
				car.turnRight(delta);
			}
			break;
		default:
			break;
		
		}
		
	}
	
	/**
	 * Turn the car counter clock wise (think of a compass going counter clock-wise)
	 */
	public  void applyLeftTurn(WorldSpatial.Direction orientation, float delta) {
		switch(orientation){
		case EAST:
			if(!car.getOrientation().equals(WorldSpatial.Direction.NORTH)){
				car.turnLeft(delta);
			}
			break;
		case NORTH:
			if(!car.getOrientation().equals(WorldSpatial.Direction.WEST)){
				car.turnLeft(delta);
			}
			break;
		case SOUTH:
			if(!car.getOrientation().equals(WorldSpatial.Direction.EAST)){
				car.turnLeft(delta);
			}
			break;
		case WEST:
			if(!car.getOrientation().equals(WorldSpatial.Direction.SOUTH)){
				car.turnLeft(delta);
			}
			break;
		default:
			break;
		
		}
		
	}
}
