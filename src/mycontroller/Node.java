package mycontroller;

import java.util.ArrayList;

import utilities.Coordinate;

public class Node {
	private Node parent;
	private Coordinate coodinate;
	private ArrayList<Node> children = new ArrayList<Node>();

	public Node(Coordinate down, Node parent, ArrayList<Node> arrayList) {
		super();
		this.setParent(parent);
		this.coodinate = parent.getCoord();
		children = arrayList;
	}

	private void setParent(Node parent2) {
		// TODO Auto-generated method stub
		this.setParent(parent2);
	}

	public int getX() {
		// TODO Auto-generated method stub
		return coodinate.x;
	}
	public int getY() {
		// TODO Auto-generated method stub
		return coodinate.y;
	}


	public Coordinate getCoord() {
		// TODO Auto-generated method stub
		return parent.getCoord();
	}

	public void setChildren(ArrayList<Node> unvisitedAdjacents) {
		this.children = unvisitedAdjacents;
	}

	
}
