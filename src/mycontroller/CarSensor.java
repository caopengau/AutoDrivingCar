package mycontroller;

import java.util.HashMap;

import tiles.LavaTrap;
import tiles.MapTile;
import tiles.TrapTile;
import utilities.Coordinate;
import world.Car;
import world.World;
import world.WorldSpatial;
import world.WorldSpatial.RelativeDirection;

public class CarSensor {
	private Car car;

	private HashMap<Coordinate, MapTile> memory;
	private int MaxMapSize = 100;
	private int sensorRange = 9;
	
	private char[][] memoryMap = new char[MaxMapSize][MaxMapSize];
	
	public CarSensor(Car car) {
		super();
		this.car = car;


		memory = new HashMap<Coordinate, MapTile>();
		
		for(int i=0;i<World.MAP_WIDTH;i++) {
			for(int j=0;j<World.MAP_HEIGHT;j++) {
				memory.put(new Coordinate(i, j), null);
				memoryMap[i][j] = '-';
				System.out.print(memoryMap[i][j]);
			}
			System.out.println();
		}
		
	}

	/**
	 * no need to turn if the car is heading to areas with unknown
	 * @return
	 */
	public boolean skipTurning() {
		Coordinate currPos = new Coordinate(car.getPosition());
		
		switch (car.getOrientation()){
		case EAST:
			for(int i = currPos.x + 1; i<World.MAP_WIDTH; i++) {
				for(int j = currPos.y - (sensorRange + 1)/2; j<currPos.y + (sensorRange - 1)/2; j++) {
					if(i<0 || j<0 || i>=World.MAP_WIDTH || j>=World.MAP_HEIGHT) continue;
					
					if(memoryMap[i][j]=='-') {
						return true;
					}
				}
			}
			break;
		case WEST:
			for(int i = 0; i<currPos.x-1; i++) {
				for(int j = currPos.y - (sensorRange + 1)/2; j<currPos.y + (sensorRange - 1)/2; j++) {

					if(i<0 || j<0 || i>=World.MAP_WIDTH || j>=World.MAP_HEIGHT) continue;
					if(memoryMap[i][j]=='-') {
						return true;
					}
				}
			}
			break;
		case NORTH:
			for(int i = currPos.x - (sensorRange + 1)/2; i < currPos.x + (sensorRange - 1)/2; i++) {
				for(int j = currPos.y; j<World.MAP_HEIGHT; j++) {

					if(i<0 || j<0 || i>=World.MAP_WIDTH || j>=World.MAP_HEIGHT) continue;
					if(memoryMap[i][j]=='-') {
						return true;
					}
				}
			}
			break;
		case SOUTH:
			for(int i = currPos.x - (sensorRange + 1)/2; i < currPos.x + (sensorRange - 1)/2; i++) {
				for(int j = 0; j<currPos.y-1; j++) {

					if(i<0 || j<0 || i>=World.MAP_WIDTH || j>=World.MAP_HEIGHT) continue;
					if(memoryMap[i][j]=='-') {
						return true;
					}
				}
			}
			break;
		}
		return false;
		
	}
	
	public Coordinate getUnknown() {
		for(int i = 0; i<World.MAP_WIDTH;i++) {
			for(int j = 0; j<World.MAP_HEIGHT;j++) {
				if(memoryMap[i][j]=='-') {
					return new Coordinate(i, j);
				}
			}
		}
		return null;
		
	}
	
	public RelativeDirection recommendDir(HashMap<Coordinate, MapTile> currentView, Coordinate target){
		if(target==null) {
			return null;
		}
		
		Coordinate currPos = new Coordinate(car.getPosition());
		switch (car.getOrientation()){
		case EAST:
			if(target.y>=currPos.y) return WorldSpatial.RelativeDirection.LEFT;
			else	return WorldSpatial.RelativeDirection.RIGHT;
		case WEST:
			if(target.y>currPos.y) return WorldSpatial.RelativeDirection.RIGHT;
			else	return WorldSpatial.RelativeDirection.LEFT;
		case NORTH:
			if(target.x>=currPos.x) return WorldSpatial.RelativeDirection.RIGHT;
			else	return WorldSpatial.RelativeDirection.LEFT;
		case SOUTH:
			if(target.x>currPos.x) return WorldSpatial.RelativeDirection.LEFT;
			else	return WorldSpatial.RelativeDirection.RIGHT;
		}
		
		return null;
	}
	
	/**
	 * print the (un)visited map
	 */
	public void printMemoryMap() {
		for(int i=World.MAP_HEIGHT-1;i>=0;i--) {
			for(int j=0;j<World.MAP_WIDTH;j++) {
				System.out.print(memoryMap[j][i]);
			}
			System.out.println();
		}
	}
	
	
	/**
	 * update the exploration of the map
	 */
	public void updateMemory() {
		Coordinate currentPosition = new Coordinate(car.getPosition());
		HashMap<Coordinate, MapTile> currentView = car.getView();
		MapTile tile;

		for(int i = -(sensorRange-1)/2; i <= (sensorRange-1)/2; i++){
			for(int j = -(sensorRange-1)/2; j <= (sensorRange-1)/2; j++) {
				Coordinate coord = new Coordinate(currentPosition.x+i, currentPosition.y+j);
				tile = currentView.get(coord);
				if(currentPosition.x+i<0 || currentPosition.x+i>=World.MAP_WIDTH || currentPosition.y+j<0 || currentPosition.y+j >= World.MAP_HEIGHT) continue;
				
				if(memory.get(coord)==null) {
					memory.put(coord, tile);
				}
				memoryMap[currentPosition.x+i][currentPosition.y+j] = '+';
			}
		}
		memoryMap[currentPosition.x][currentPosition.y] = 'O';
	}
	
	/**
	 * this function checks if the tile is a lava tile
	 * @param tile
	 * @return
	 */
	public boolean isLavaTile(MapTile tile) {
		if(tile!=null && tile.getType()==MapTile.Type.TRAP)
			if(((TrapTile)tile).getTrap().equals("lava")) {
				return true;
			}
		
		return false;
	}
	
	/**
	 * Check if you have a tileType in front of you dist away!
	 * @param orientation the orientation we are in based on WorldSpatial
	 * @param currentView what the car can currently see
	 * @return
	 */
	public Coordinate checkTileTypeAhead(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView, int dist, MapTile.Type tileType){
		switch(orientation){
		case EAST:
			return checkEast(currentView, dist, tileType);
		case NORTH:
			return checkNorth(currentView, dist, tileType);
		case SOUTH:
			return checkSouth(currentView, dist, tileType);
		case WEST:
			return checkWest(currentView, dist, tileType);
		default:
			return null;
		
		}
	}
	/**
	 * Below 4 functions check if the tile is of tileType up to dist away in the 4 direction
	 * checkEast will check up to dist amount of tiles to the right.
	 * checkWest will check up to dist amount of tiles to the left.
	 * checkNorth will check up to dist amount of tiles to the top.
	 * checkSouth will check up to dist amount of tiles below.
	 */
	public Coordinate checkEast(HashMap<Coordinate, MapTile> currentView, int dist, MapTile.Type tileType){
		// Check tiles to my right
		
		
		Coordinate currentPosition = new Coordinate(car.getPosition());
		for(int i = 0; i <= dist; i++){
			Coordinate coord = new Coordinate(currentPosition.x+i, currentPosition.y);
			if(currentPosition.x+i>World.MAP_WIDTH) {
				return new Coordinate(World.MAP_WIDTH, currentPosition.y);
			}
			MapTile tile = currentView.get(coord);
			
			if(tile.isType(tileType)){
				return coord;
			}
		}
		return null;
	}
	
	public Coordinate checkWest(HashMap<Coordinate,MapTile> currentView, int dist, MapTile.Type tileType){
		// Check tiles to my left
		Coordinate currentPosition = new Coordinate(car.getPosition());
		for(int i = 0; i <= dist; i++){
			if(currentPosition.x-i<=0) {
				return new Coordinate(0, currentPosition.y);
			}
			
			Coordinate coord = new Coordinate(currentPosition.x-i, currentPosition.y);
			MapTile tile = currentView.get(coord);
			if(tile.isType(tileType)){
				return coord;
			}
		}
		return null;
	}
	
	public Coordinate checkNorth(HashMap<Coordinate,MapTile> currentView, int dist, MapTile.Type tileType){
		// Check tiles to towards the top
		Coordinate currentPosition = new Coordinate(car.getPosition());
		for(int i = 0; i <= dist; i++){
			Coordinate coord = new Coordinate(currentPosition.x, currentPosition.y+i);
			if(currentPosition.y+i>World.MAP_HEIGHT) {
				return new Coordinate(currentPosition.x, World.MAP_HEIGHT);
			}
			MapTile tile = currentView.get(coord);
			if(tile.isType(tileType)){
				return coord;
			}
		}
		return null;
	}
	

	/**
	 * 
	 * This function check if the tile is of tileType up to dist away in the South direction
	 */
	public Coordinate checkSouth(HashMap<Coordinate,MapTile> currentView, int dist, MapTile.Type tileType){
		// Check tiles towards the bottom
		Coordinate currentPosition = new Coordinate(car.getPosition());
		for(int i = 0; i <= dist; i++){
			Coordinate coord = new Coordinate(currentPosition.x, currentPosition.y-i);
			
			if(currentPosition.y-i<0) {
				return new Coordinate(currentPosition.x, 0);
			}
			MapTile tile = currentView.get(coord);
			if(tile.isType(tileType)){
				return coord;
			}
		}
		return null;
	}
	
	/**
	 * Check if the wall is on your left hand side given your orientation
	 * @param orientation
	 * @param currentView
	 * @return
	 */
	public Coordinate checkLeftWall(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView, int dist, MapTile.Type tileType) {
		
		switch(orientation){
		case EAST:
			return checkNorth(currentView, dist, tileType);
		case NORTH:
			return checkWest(currentView, dist, tileType);
		case SOUTH:
			return checkEast(currentView, dist, tileType);
		case WEST:
			return checkSouth(currentView, dist, tileType);
		default:
			return null;
		}	
	}
	/**
	 * Check if the wall is on your right hand side given your orientation
	 * @param orientation
	 * @param currentView
	 * @return
	 */
	public Coordinate checkRightWall(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView, int dist, MapTile.Type tileType) {
		
		switch(orientation){
		case EAST:
			return checkSouth(currentView, dist, tileType);
		case NORTH:
			return checkEast(currentView, dist, tileType);
		case SOUTH:
			return checkWest(currentView, dist, tileType);
		case WEST:
			return checkNorth(currentView, dist, tileType);
		default:
			return null;
		}	
	}
	/***********************************************************/
	/***************** check 9 * 9 tiles *********************/
	/***********************************************************/
	/**
	 * 
	 * @param currentView
	 * @param x x amount smaller than centre coord
	 * @param y y amount smaller than centre coord 
	 * @param type tiletype string "lava", "health"...
	 * @return
	 */
	public Coordinate checkRange(HashMap<Coordinate, MapTile> currentView, int x, int y, String type) {
		Coordinate currentPosition = new Coordinate(car.getPosition());
		
		Coordinate startingPos = new Coordinate(currentPosition.x + x, currentPosition.y + y);
		MapTile tile;
		
		for(int i = 0; i < (sensorRange-1)/2-1; i++) {
			Coordinate coord = new Coordinate(startingPos.x+i, startingPos.y+i);
			tile = currentView.get(coord);
			MapTile.Type tileType = tile.getType();
			if(tileType == MapTile.Type.TRAP) {
				if(((TrapTile) tile).getTrap().equals(type) && type.equals("lava") && ((LavaTrap)tile).getKey()==car.getKey()-1) {
					return coord;
				} else if(((TrapTile) tile).getTrap().equals(type) && type.equals("health")) {
					return coord;
				}
			}

		}
		
		
		return null;
		
	}
	
	/**
	 * This function checks the tile type on left front of the car
	 * @param orientation
	 * @param currentView
	 * @param type tile type "lava" or "health"...
	 * @return coordinate of the tile
	 */
	
	public Coordinate checkTileTypeAheadLeft(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView, String type){
		switch(orientation){
		case EAST:
			return checkRange(currentView, 1, 1, type);
		case NORTH:
			return checkRange(currentView, -(sensorRange-1)/2, 1, type);
		case SOUTH:
			return checkRange(currentView, 1, -(sensorRange-1)/2, type);
		case WEST:
			return checkRange(currentView, -(sensorRange-1)/2, -(sensorRange-1)/2, type);
		default:
			return null;
		}
		
	}
	/**
	 * This function checks the tile type on right front of the car
	 * @param orientation
	 * @param currentView
	 * @param type tile type "lava" or "health"...
	 * @return coordinate of the tile
	 */
	public Coordinate checkTileTypeAheadRight(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView, String type){
		switch(orientation){
		case EAST:
			return checkRange(currentView, 1, -(sensorRange-1)/2, type);
		case NORTH:
			return checkRange(currentView, 1, 1, type);
		case SOUTH:
			return checkRange(currentView, -(sensorRange-1)/2, -(sensorRange-1)/2, type);
		case WEST:
			return checkRange(currentView, -(sensorRange-1)/2, 1, type);
		default:
			return null;
		}
		
	}
	
	/**
	 * This function checks the tile type on front of the car 
	 * @param orientation
	 * @param currentView
	 * @param type tile type "lava" or "health"...
	 * @return coordinate of the tile
	 */
	public Coordinate checkTileAhead(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView, String type){
		
		Coordinate  coord;
		if((coord = checkTileTypeAheadRight(orientation, currentView, type))!= null){
			return coord;
		};
		coord = checkTileTypeAheadLeft(orientation, currentView, type);
		return coord;
	}
	
	
	
}
