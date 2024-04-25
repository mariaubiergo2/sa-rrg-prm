package com.recuit;

import java.util.HashSet;
import java.util.Random;

import com.recuit.interfaces.PointInterface;

public class Point2D implements PointInterface {

    private String id;
	private int x;
	private int y;
    private HashSet<String> freeNeighbors;
		
    public Point2D() {

    }

	// Constructor
	public Point2D(int x, int y) {
		this.x = x;
		this.y = y;
		this.id = String.valueOf(x)+' '+String.valueOf(y);
        this.freeNeighbors = new HashSet<String>();
	}


    // Constructor of a random Point2D
	// given the bounderies set by minX, maxX, minY, maxY
	public Point2D(int minX, int maxX, int minY, int maxY){
		this.x = (int) ((Math.random()*(maxX-minX) + minX));
		this.y = (int) ((Math.random()*(maxY-minY) + minY));
		this.id = String.valueOf(this.x)+' '+String.valueOf(this.y);
        this.freeNeighbors = new HashSet<String>();
	}


    // Constructor
	public Point2D(String identification) {
		this.x = Integer.parseInt(identification.split(" ")[0]);
		this.y = Integer.parseInt(identification.split(" ")[1]);
		this.id = identification;
		this.freeNeighbors = new HashSet<String>();
	}

	@Override
	public PointInterface copy() {
        return new Point2D(this.x, this.y); // Create a new Point2D instance with the same coordinates
    }


    // Getter of X
	public int getX(){
		return this.x;
	}
	
	// Getter of Y
	public int getY(){
		return this.y;
	}

    @Override
    public HashSet<String> getNeighbors(){
		return this.freeNeighbors;
	}


    @Override
    public void setNeighbor(PointInterface neighbor){
        Point2D otherPoint = (Point2D) neighbor;
		this.freeNeighbors.add(otherPoint.getId());
	}


    @Override
    public String getRandomNeighbor(){
		String[] neighborArray = this.freeNeighbors.toArray(new String[0]);
		
		int randomIndex = Manager.random.nextInt(neighborArray.length);
		
		return neighborArray[randomIndex];
	}


    @Override
    public int[] getCoordinates() {
        return new int[]{this.x, this.y};
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public double distanceTo(PointInterface other) {
        if(other instanceof Point2D){
			Point2D otherPoint = (Point2D) other;
			int deltaX = otherPoint.getX() - this.x;
			int deltaY = otherPoint.getY() - this.y;
			return Math.sqrt(deltaX*deltaX + deltaY*deltaY);
		}
		else{
			throw new IllegalArgumentException("The point is not a 2D point!");
		}
    }

    @Override
    public double angle(PointInterface other) {
        Point2D otherPoint = (Point2D) other;
        int deltaX = otherPoint.getX() - this.x;
		int deltaY = otherPoint.getY() - this.y;

        return Math.atan2(deltaY, deltaX);
    }

    @Override
    public PointInterface newPoint(double distance, double angle) {
        return new Point2D((int) (this.x + distance*Math.cos(angle)), (int)(this.y + distance*Math.sin(angle)));
    }
    
    
}
