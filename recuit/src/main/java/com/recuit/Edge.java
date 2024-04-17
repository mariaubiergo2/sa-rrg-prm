package com.recuit;

import com.recuit.interfaces.PointInterface;

// Represents an edge between two points
public class Edge {
    
    private PointInterface p1; // One endpoint of the edge
    private PointInterface p2; // Another endpoint of the edge

    // Constructor to initialize the edge with two points
    public Edge (PointInterface p1, PointInterface p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    // Method to check if the edge connects a given node
    public boolean connects(PointInterface node) {
        return p1.getId().equals(node.getId()) || p2.getId().equals(node.getId());
    }

    // Method to get the other end of the edge given one end
    public PointInterface getOtherEnd(PointInterface node) {
        if (p1.getId().equals(node.getId())) {
            return p2;
        } else if (p2.getId().equals(node.getId())) {
            return p1;
        } else {
            throw new IllegalArgumentException("The given node is not part of this edge.");
        }
    }

    public PointInterface getStart(){
        return p1;
    }
    
    public PointInterface getEnd(){
        return p2;
    }

    // Method to calculate the distance between the two endpoints of the edge
    public double distance() {
        return p1.distanceTo(p2);
    }

    // Method to print the edge
    public String printEdge(){
        String res = p1.getId()+" "+p2.getId();
        return res;
    }

    // Method to check if this edge is the reverse of another edge
    public boolean isReverseOf(Edge other) {
        return (p1.equals(other.p2) && p2.equals(other.p1));
    }

    public boolean isSameAs(Edge other){
        return (p1.equals(other.p1) && p2.equals(other.p2));
    }

    // Method to check if the edge starts and ends at the same point
    public boolean startAndEndAtSamePoint() {
        return p1.equals(p2);
    }

}
