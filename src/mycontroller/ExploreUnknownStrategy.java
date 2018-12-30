package mycontroller;

import tiles.MapTile;
import utilities.Coordinate;
import world.Car;
import world.WorldSpatial;

public class ExploreUnknownStrategy implements Strategy{
	
	// initial variables
	private Car car;
	private CarMovement cm;
	private CarSensor cs;
	private StopWatch sw;
	private String strategyName;

	
	
	// movement variables
	private boolean leftTrueRightFalse = false;
	private boolean isTurning = false;
	private float minSpeed = 3f;
	private float turningSpeed = 1f;
	private int decelerateBeforeNtile = 3;
	private int turningDistToWall = 1;
	private boolean moved = false;
	private Object lastTurnDirection;
	private boolean needAdjust = false;

	
	
	public ExploreUnknownStrategy(Car car, CarSensor cs, CarMovement cm) {
		super();
		this.car = car;
		this.cm = cm;
		this.cs = cs;
		
		sw = new StopWatch();
		sw.start();
		
		strategyName = "RandomMoveStrategy";
	}

	
	@Override
	public void getMoving(float delta) {

		
		if(car.getSpeed() == 0 && moved) {
			moved = false;
			unstuck(delta);
		}
		
		
		if(isTurning) {
			WorldSpatial.RelativeDirection dir;
			if(leftTrueRightFalse)
				dir = WorldSpatial.RelativeDirection.LEFT;
			else
				dir = WorldSpatial.RelativeDirection.RIGHT;

			if(cm.turnDir(dir, delta)) {
				isTurning = false;
				needAdjust = true;
				this.lastTurnDirection = dir;
				sw.start();
			}
		} else {
			
			// recommanded direction to turn towards unknown area
			if(cs.getUnknown()==null) {
				System.out.println("=================finish exploring====================");
				System.out.println("=============================================");
				System.out.println("=============================================");
				System.out.println("=============================================");
			}
			WorldSpatial.RelativeDirection recDir = cs.recommendDir(car.getView(), cs.getUnknown());
			
			// hitting a wall so have to decide a direction to turn
			if(cs.checkTileTypeAhead(car.getOrientation(), car.getView(), turningDistToWall, MapTile.Type.WALL)!=null) {

				// leftTrueRightFalse = Math.random() < 0.5;
				if(recDir==WorldSpatial.RelativeDirection.LEFT) {
					leftTrueRightFalse = true;
				}else if(recDir==WorldSpatial.RelativeDirection.RIGHT){
					leftTrueRightFalse = false;
				}else { // all area explored we random turn
					leftTrueRightFalse = Math.random() < 0.5;
				}
				
				isTurning = true;
				
				// however, do not be stupid
				// left is wall, so only turn right available
				if(cs.checkLeftWall(car.getOrientation(), car.getView(), turningDistToWall, MapTile.Type.WALL)!=null) {
					leftTrueRightFalse = false;
				}
				// right is wall, so only turn left available
				if(cs.checkRightWall(car.getOrientation(), car.getView(), turningDistToWall, MapTile.Type.WALL)!=null) {
					leftTrueRightFalse = true;
				}
				
			} 
			
			// not hitting wall, but need to try to turn towards unknown area
			else {
				
//				if(cs.skipTurning())
//					isTurning = false;
//					
//				}else 
				if(recDir==WorldSpatial.RelativeDirection.LEFT) {
					if(cs.checkLeftWall(car.getOrientation(), car.getView(), turningDistToWall, MapTile.Type.WALL)==null) {
						isTurning = true;
						leftTrueRightFalse = true;
					}
				}else {
					if(cs.checkRightWall(car.getOrientation(), car.getView(), turningDistToWall, MapTile.Type.WALL)==null) {
						isTurning = true;
						leftTrueRightFalse = false;
					}
				}
			}
			
		}
		
		// adjust to 90% orientation
		if(needAdjust)
			if(cm.adjust(delta)) {
				needAdjust = false;
			}
		
		
		// always accelerate if we are passing lava
		boolean inLava = cm.getPassLavaFast();
		if(inLava) {
			modeLavaSpeed();
		}else {
			modeNormalSpeed();
		}
		
		// maintain speed;
		if(cs.checkTileTypeAhead(car.getOrientation(), car.getView(), decelerateBeforeNtile, MapTile.Type.WALL)!=null) {
			if(car.getSpeed()<turningSpeed) car.applyForwardAcceleration();
			if(car.getSpeed()>turningSpeed && !inLava) car.applyReverseAcceleration();
		} else {
			if(car.getSpeed()<minSpeed) car.applyForwardAcceleration();
		}

		
		moved = true;
		
	}

	@Override
	public String getStrategyName() {
		// TODO Auto-generated method stub
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
			if (leftTrueRightFalse) {
				car.turnLeft(delta);
			} else {
				car.turnRight(delta);
			}
		}
	}
	
	public void modeNormalSpeed() {
		minSpeed = 3f;
		turningSpeed = 1f;
		decelerateBeforeNtile = 3;
		turningDistToWall = 1;
	}
	
	public void modeLavaSpeed() {
		minSpeed = 4f;
		turningSpeed = 2f;
		decelerateBeforeNtile = 3;
		turningDistToWall = 3;
	}
}
