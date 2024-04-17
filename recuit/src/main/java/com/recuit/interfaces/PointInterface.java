package com.recuit.interfaces;

import java.util.HashSet;

public interface PointInterface {

    // Get the coodinates of a certain point
	int[] getCoordinates();
	
	// Get the identifier of that certain point
	String getId();

    // Get the distance to another point
	public double distanceTo(PointInterface other);

    // Get the angle at which there is the other point
	public double angle(PointInterface other);

    // Generate another point at a certain distance and angle from that one
	public PointInterface newPoint(double distance, double angle);

    void setNeighbor(PointInterface neighbor);

    String getRandomNeighbor();

    // For testing
    HashSet<String> getNeighbors();

    public PointInterface copy();
    
}
