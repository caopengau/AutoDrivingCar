package mycontroller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import tiles.MapTile;
import tiles.TrapTile;
import utilities.Coordinate;
import world.Car;
import world.World;

public class CarMemory {
	
	private Car car;
	private String carSym = "@";	// depending on facing direction
	private final String unknown = "`";
	private final String road = "=";
	private final String lava = "-";
	private final String health = "+";
	private final String wall = "x";
	private final String finish = "!";
	private final String start = "@";
	
	// print memory controls
	private boolean printFlag = true;
	private boolean firstTime = true;
	private HashMap<Coordinate, MapTile> oldView = new HashMap<>(); 
	private HashMap<Coordinate, String> memoryMap = new HashMap<Coordinate, String>();

	
	// pathfinding variables
	private HashMap<Coordinate, Integer> pathMarker = new HashMap<>();
	private ArrayList<Node> queueList = new ArrayList<Node>();
	private ArrayList<Node> path = new ArrayList<>();
	
	public CarMemory(Car car) {
		super();
		this.car = car;		
		for(int i = 0; i < World.MAP_WIDTH; i++) {
			for(int j = 0; j < World.MAP_HEIGHT; j++) {
				memoryMap.put(new Coordinate(i,j), unknown);
				pathMarker.put(new Coordinate(i,j), -1);
			}
		}
		oldView = car.getView();
		updateMapMemory();
		showMemory();
		
	}
	
	public void updateMapMemory() {
		carFacing();
		HashMap<Coordinate,MapTile> carView = car.getView();
		
		// nothing to update, car stationary
		if(oldView.keySet().equals(car.getView().keySet()) && !firstTime) {
			return;
		}
		for(Coordinate coord: carView.keySet()) {
			if(carView.get(coord).isType(MapTile.Type.WALL) && !memoryMap.get(coord).equals(wall)) {
				memoryMap.replace(coord, wall); printFlag = true;
			}
			else if(carView.get(coord).isType(MapTile.Type.ROAD) && !memoryMap.get(coord).equals(road)) {
				memoryMap.replace(coord, road); printFlag = true;
			}
			else if(carView.get(coord) instanceof TrapTile) {
				TrapTile trapTile = (TrapTile) carView.get(coord);
				if(trapTile.getTrap().equals("lava") && !memoryMap.get(coord).equals(lava)) {
					memoryMap.replace(coord, lava); printFlag = true;
				}
				else if(trapTile.getTrap().equals("health") && !memoryMap.get(coord).equals(health)) {
					memoryMap.replace(coord, health); printFlag = true;
				}
			} 
			else if(carView.get(coord).getType() == MapTile.Type.FINISH && !memoryMap.get(coord).equals(finish)) {
				memoryMap.replace(coord, finish); printFlag = true;
			} 
			else if(carView.get(coord).getType() == MapTile.Type.START && !memoryMap.get(coord).equals(start)) {
				memoryMap.replace(coord, start); printFlag = true;
			}
		}
		memoryMap.replace(new Coordinate(car.getPosition()), carSym);
		firstTime = false;
		oldView = car.getView();
		
		path.clear();
		// getPath(new Coordinate(2,3), new Coordinate(3,3), 0);
		
		for(Node c: path) {
			System.out.print(c + "<-");
		}
	}
	
	public void showMemory() {
		if(!printFlag)
			return;
		System.out.println("*******************************************");
		for(int i = World.MAP_HEIGHT - 1; i >= 0 ; i--) {
			for(int j = 0; j < World.MAP_WIDTH; j++) {
				System.out.print(memoryMap.get(new Coordinate(j,i)));
			}
			System.out.println();
		}
		System.out.println("*******************************************");
		printFlag = false;
	}
	
	private void carFacing() {
		switch (car.getOrientation()) {
			case EAST:
				carSym = ">";
				break;
			case WEST:
				carSym = "<";
				break;
			case NORTH:
				carSym = "^";
				break;
			case SOUTH:
				carSym = "v";
				break;
			default:
				carSym = "@";
				break;
		}
	}
	
	private ArrayList<Node> getUnvisitedAdjacents(Node start) {
		ArrayList<Node> result = new ArrayList<Node>();
		
		if(start.getY()+1<World.MAP_HEIGHT && !memoryMap.get(new Coordinate(start.getX(), start.getY()+1)).equals(unknown) && pathMarker.get(new Coordinate(start.getX(), start.getY()+1))==-1) {
			Coordinate up = new Coordinate(start.getX(), start.getY()+1);
			result.add(new Node(up, start, new ArrayList<Node>()));
		}
		
		if(start.getY()-1>=0 && !memoryMap.get(new Coordinate(start.getX(), start.getY()+1)).equals(unknown) && pathMarker.get(new Coordinate(start.getX(), start.getY()-1))==-1) {
			Coordinate down = new Coordinate(start.getX(), start.getY()-1);
			result.add(new Node(down, start, new ArrayList<Node>()));
		}
		
		if(start.getX()-1>=0 && !memoryMap.get(new Coordinate(start.getX(), start.getY()+1)).equals(unknown) && pathMarker.get(new Coordinate(start.getX()-1, start.getY()))==-1) {
			Coordinate left = new Coordinate(start.getX()-1, start.getY());
			result.add(new Node(left, start, new ArrayList<Node>()));
		}
		
		if(start.getX()+1<World.MAP_WIDTH && !memoryMap.get(new Coordinate(start.getX(), start.getY()+1)).equals(unknown) && pathMarker.get(new Coordinate(start.getX()+1, start.getY()))==-1) {
			Coordinate right = new Coordinate(start.getX()+1, start.getY());
			result.add(new Node(right, start, new ArrayList<Node>()));
		}
		
		
		return result;
	}
	
	public void buildNodeTree() {
		// reset
		resetPathMarker();
		queueList.clear();
		
		// make start coord as root
		Coordinate start = new Coordinate(car.getPosition());
		Node root = new Node(start, null, new ArrayList<Node>());
		queueList.add(root);
		this.pathMarker.replace(root.getCoord(), 1);
		
		while (queueList.size()>0) {
			System.out.println(queueList.get(0));
			this.pathMarker.replace(root.getCoord(), 1);
			root.setChildren(getUnvisitedAdjacents(queueList.get(0)));
			queueList.remove(0);
		}
		
		
	}
	

	public void resetPathMarker() {
		for(Coordinate c: pathMarker.keySet()) {
			pathMarker.replace(c, -1);
		}
	}
}
