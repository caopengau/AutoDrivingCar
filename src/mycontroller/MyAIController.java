package mycontroller;

import controller.CarController;
import tiles.MapTile;
import utilities.Coordinate;
import world.Car;

public class MyAIController extends CarController{

	private Car car;
	private CarMovement cm;
	private CarSensor cs;
	
	// strategy variables
	private ExploreUnknownStrategy eus;
	private MoveAlongStrategy mas;
	private HealStrategy hs;
	
	
	// loop control
	private StopWatch sw;
	private String oldPos;
	private boolean inLoop = false;
	
	public MyAIController(Car car) {
		super(car);
		this.car = car;
		
		// inLoop avoidance variables
		sw = new StopWatch();
		sw.start();
		oldPos = car.getPosition();
		
		// car sensor and movement
		cs = new CarSensor(car);
		cm = new CarMovement(car, cs);
		
		
		// strategies
		eus = new ExploreUnknownStrategy(car, cs, cm);
		mas = new MoveAlongStrategy(car, cs, cm);
		hs = new HealStrategy(car, cs, cm);
		
	}

	
	@Override
	public void update(float delta) {
		
		cs.updateMemory();
		
		
		if(sw.getElapsedTimeSecs()>2) {	// loop avoidance
			if(oldPos.equals(car.getPosition())) {
				inLoop = true;
			} else {
				inLoop = false;
			}
			oldPos = car.getPosition();
			sw.start();
		}

		System.out.println(inLoop);
		if(!inLoop)
			eus.getMoving(delta);
		else
			mas.getMoving(delta);
		
		hs.getMoving(delta);

	}
	
	

}
