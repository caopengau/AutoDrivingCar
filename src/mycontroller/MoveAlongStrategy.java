package mycontroller;
/**
 * This strategy helps the car to move along the left side wall
 */
import java.util.HashMap;

import tiles.MapTile;
import tiles.TrapTile;
import utilities.Coordinate;
import world.Car;
import world.WorldSpatial;

public class MoveAlongStrategy implements Strategy{
	
	private Car car;
	
	private boolean isFollowingWall = false; // This is initialized when the car sticks to a wall.
	public static WorldSpatial.RelativeDirection getLastTurnDirection() {
		return lastTurnDirection;
	}

	public static void setLastTurnDirection(WorldSpatial.RelativeDirection lastTurnDirection) {
		MoveAlongStrategy.lastTurnDirection = lastTurnDirection;
	}

	public static WorldSpatial.RelativeDirection lastTurnDirection = null; // Shows the last turn direction the car takes.
	private boolean isTurningLeft = false;
	private boolean isTurningRight = false; 
	private WorldSpatial.Direction previousState = null; // Keeps track of the previous state
	private boolean moved = false;	
	
	// Car Speed to move at
	private final float CAR_SPEED = 3f;
	

	
	// Offset used to differentiate between 0 and 360 degrees
	private int EAST_THRESHOLD = 3;
	
	
	boolean notSouth = true;

	private String strategyName;

	private CarSensor cs;
	private CarMovement cm;
	
	
	public MoveAlongStrategy(Car car, CarSensor cs, CarMovement cm) {
		super();
		this.car = car;

		this.cs = cs;
		this.cm = cm;
		strategyName = "MoveAlongStrategy";
	}
	
	@Override
	public void getMoving(float delta) {
		// Gets what the car can see
		HashMap<Coordinate, MapTile> currentView = car.getView();
		checkStateChange();


		
		// If you are not following a wall initially, find a wall to stick to!

		// If you are not following a wall initially, find a wall to stick to!
		
		if (car.getSpeed() == 0 && moved) {
			moved = false;
			unstuck(delta);
		}
		
		if(cs.checkLeftWall(car.getOrientation(),currentView, 1, MapTile.Type.WALL)==null){
			isFollowingWall = true;
		}
		
		
		if(!isFollowingWall){
			if(car.getSpeed() < CAR_SPEED){
				car.applyForwardAcceleration();
			}
			// Turn towards the north
			if(!car.getOrientation().equals(WorldSpatial.Direction.NORTH)){
				lastTurnDirection = WorldSpatial.RelativeDirection.LEFT;
				cm.applyLeftTurn(car.getOrientation(),delta);

			}
			if(cs.checkNorth(currentView, 2, MapTile.Type.WALL) != null){
				// Turn right until we go back to east!
				if(!car.getOrientation().equals(WorldSpatial.Direction.EAST)){
					lastTurnDirection = WorldSpatial.RelativeDirection.RIGHT;
					cm.applyRightTurn(car.getOrientation(),delta);
				}
				else{
					isFollowingWall = true;
				}
			}
		}
		// Once the car is already stuck to a wall, apply the following logic
		else{
			// speed up when we not turning
			if(car.getSpeed()<CAR_SPEED) {
				car.applyForwardAcceleration();
			}
			
			// Readjust the car if it is misaligned.
			readjust(lastTurnDirection,delta);

			
			if(isTurningRight){
				cm.applyRightTurn(car.getOrientation(),delta);
			}
			else if(isTurningLeft){
				// Apply the left turn if you are not currently near a wall.
				if(cs.checkLeftWall(car.getOrientation(),currentView, 1, MapTile.Type.WALL)==null){
					cm.applyLeftTurn(car.getOrientation(),delta);
				}
				else{
					isTurningLeft = false;
				}
			}
			// Try to determine whether or not the car is next to a wall.
			else if(cs.checkLeftWall(car.getOrientation(),currentView, 1, MapTile.Type.WALL)!=null){
				// Maintain some velocity
				if(car.getSpeed() < CAR_SPEED){
					car.applyForwardAcceleration();
				}
				// If there is wall ahead, turn right!
				if(cs.checkTileTypeAhead(car.getOrientation(),currentView, 2, MapTile.Type.WALL)!=null){
					lastTurnDirection = WorldSpatial.RelativeDirection.RIGHT;
					isTurningRight = true;				
					
				}

			}
			// This indicates that I can do a left turn if I am not turning right
			else{
				lastTurnDirection = WorldSpatial.RelativeDirection.LEFT;
				isTurningLeft = true;
			}
		}
		
		moved = true;
	}
	/**
	 * Checks whether the car's state has changed or not, stops turning if it
	 *  already has.
	 */
	private void checkStateChange() {
		if(previousState == null){
			previousState = car.getOrientation();
		}
		else{
			if(previousState != car.getOrientation()){
				if(isTurningLeft){
					isTurningLeft = false;
				}
				if(isTurningRight){
					isTurningRight = false;
				}
				previousState = car.getOrientation();
			}
		}
	}

	/**
	 * Readjust the car to the orientation we are in.
	 * @param lastTurnDirection
	 * @param delta
	 */
	private void readjust(WorldSpatial.RelativeDirection lastTurnDirection, float delta) {
		if(lastTurnDirection != null){
			if(!isTurningRight && lastTurnDirection.equals(WorldSpatial.RelativeDirection.RIGHT)){
				adjustRight(car.getOrientation(),delta);
			}
			else if(!isTurningLeft && lastTurnDirection.equals(WorldSpatial.RelativeDirection.LEFT)){
				adjustLeft(car.getOrientation(),delta);
			}
		}
		
	}
	
	/**
	 * Try to orient myself to a degree that I was supposed to be at if I am
	 * misaligned.
	 */
	private void adjustLeft(WorldSpatial.Direction orientation, float delta) {
		
		switch(orientation){
		case EAST:
			if(car.getAngle() > WorldSpatial.EAST_DEGREE_MIN+EAST_THRESHOLD){
				car.turnRight(delta);
			}
			break;
		case NORTH:
			if(car.getAngle() > WorldSpatial.NORTH_DEGREE){
				car.turnRight(delta);
			}
			break;
		case SOUTH:
			if(car.getAngle() > WorldSpatial.SOUTH_DEGREE){
				car.turnRight(delta);
			}
			break;
		case WEST:
			if(car.getAngle() > WorldSpatial.WEST_DEGREE){
				car.turnRight(delta);
			}
			break;
			
		default:
			break;
		}
		
	}

	private void adjustRight(WorldSpatial.Direction orientation, float delta) {
		switch(orientation){
		case EAST:
			if(car.getAngle() > WorldSpatial.SOUTH_DEGREE && car.getAngle() < WorldSpatial.EAST_DEGREE_MAX){
				car.turnLeft(delta);
			}
			break;
		case NORTH:
			if(car.getAngle() < WorldSpatial.NORTH_DEGREE){
				car.turnLeft(delta);
			}
			break;
		case SOUTH:
			if(car.getAngle() < WorldSpatial.SOUTH_DEGREE){
				car.turnLeft(delta);
			}
			break;
		case WEST:
			if(car.getAngle() < WorldSpatial.WEST_DEGREE){
				car.turnLeft(delta);
			}
			break;
			
		default:
			break;
		}
		
	}

	@Override
	public String getStrategyName() {
		return strategyName;
	}

	/**
	 * This funciton unstuck the car if we hit the wall and stop moving
	 * @param delta
	 */
	
	public void unstuck(float delta) {
		//reverse
		car.applyReverseAcceleration();
		//and turn in the opposite direction
		if(lastTurnDirection != null) {
			if (isTurningLeft) {
				car.turnLeft(delta);
			} else {
				car.turnRight(delta);
			}
		}
	}
}
