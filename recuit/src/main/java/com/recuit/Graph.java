package com.recuit;

import java.util.*;

import org.bson.Document;

import com.mongodb.client.model.Filters;
import com.recuit.edgesInformation.MongoDBConnection;
import com.recuit.interfaces.PointInterface;

public class Graph<T extends PointInterface> {
    
    List<T> nodes; // List of nodes in the graph
    List<Edge> edges; // List of edges in the graph

    
    public Graph() {
        this.nodes = new ArrayList<>();
        this.edges = new ArrayList<>();
    }

    // Constructor to initialize a graph with provided nodes and edges
    public Graph(List<T> nodes, List<Edge> edges) {
        this.nodes = nodes;
        this.edges = edges;
    }


    public List<Edge> getEdges() {
        return this.edges;
    }

    
    public String printGraph() {
        String buffer = "";
        buffer = buffer + "Nnodes= " + nodes.size();
        buffer = buffer + "; Nedges= " + edges.size();
        return buffer;
    }

    // Method to add a node to the graph
    public void addNodes(T point) {
        this.nodes.add(point);
    }

    // *************************************************************************
    // COST FUNCTIONS
    // *************************************************************************

    public double evaluateMe() {
        double maxEdge = computeMaxDistanceToABase(Manager.basesDictionary);
        
        double penalty = Recuit.PENALTY;
        if (maxEdge==0.0){
            return penalty; // Works as a penalty
        }

        if (this.edges.size()==0){
            return penalty;
        } else {
            if (!isConnected()) {
                return penalty;
            }
        }

        return maxEdge;
    }

    public double computeMaxDistanceToABase(HashSet<String> bases){

        double maxDistance = 0.0;
        double edgeDist = 0.0;
        for(Edge edge : this.edges){
            edgeDist = edge.distance();
            if (edgeDist>maxDistance){
                if (bases.contains(edge.getStart().getId()) || bases.contains(edge.getEnd().getId())){
                    maxDistance=edgeDist;
                }
                
            }
        }
        return maxDistance;
    }
    
    // Second cost function
    // Method to evaluate the graph based on connectivity and total distance
    public double evaluateMeConnectivityAndMaxDistance() {
        double maxEdge = computeMaxEdgeDistance();

        double penalty = Recuit.PENALTY;
        if (maxEdge==0.0){
            return penalty; // Works as a penalty
        }

        if (this.edges.size()==0){
            return penalty;
        } else {
            if (!isConnected()) {
                return penalty;
            }
        }

        return maxEdge;
    }

    public double evaluateMeConnectivityAndIntersections(){ 
        
        double penalty = Recuit.PENALTY;
        

        if (this.edges.size()==0){
            return penalty;
        } else {
            
            if (!isConnected()) {
                return penalty;
            }
            

			
            else {
                return (countEdgeIntersections()+computeMaxEdgeDistance());
            }
            

        }

    }

    public double computeMaxEdgeDistance(){
        double maxDistance = 0.0;
        for(Edge edge : this.edges){
            if (edge.distance()>maxDistance){
                maxDistance=edge.distance();
            }
        }
        return maxDistance;
    }
    
    // First cost function
    // Method to evaluate the graph based on connectivity and total distance
    public double evaluateMeConnectivityAndTotalDistance() {

        double penalty = 0.0;
        if (!isConnected()) {
            penalty = 1.0;
        }
        if (this.edges.size() == 0) {
            penalty = 1.0;
        }

        double totalDistance = computeTotalDistance();

        double costFunction = totalDistance + penalty * 30000;

        return costFunction;
    }

    // Method to compute the total distance of the graph
    public double computeTotalDistance() {
        double totalDistance = 0.0;
        for (Edge edge : edges) {
            totalDistance += edge.distance();
        }
        return totalDistance;
    }



    // Method to check if the graph is connected
    private boolean isConnected() {

        if (this.edges.size()==0) {
            // If there are no nodes, the graph is considered connected
            // But! we penalize that there are no nodes
            return false;
        } else if (edges.size() < nodes.size() - 1) {
            return false;
        }

        // Choose the first node as the starting point for DFS
        T startNode = nodes.get(0);

        // Set to keep track of visited nodes
        Set<String> visited = new HashSet<>();

        // Perform DFS traversal
        dfs(startNode, visited);

        // Check if all nodes are visited
        return areNodesVisited(nodes, visited);
    }

    public static <T> boolean areNodesVisited(List<T> nodes, Set<String> visited) {
        for (T node : nodes) {
            if (!visited.contains(((PointInterface) node).getId())) {
                return false; // Found an element in nodes that is not visited
            }
        }
        return true; // All elements in nodes are visited
    }

    // Depth-first search (DFS) traversal
    private void dfs(T node, Set<String> visited) {
        visited.add(node.getId());
        for (Edge edge : edges) {
            if (edge.connects(node)) {
                @SuppressWarnings("unchecked")
                T neighbor = (T) edge.getOtherEnd(node);
                if (!visited.contains(neighbor.getId())) {
                    dfs(neighbor, visited);
                }
            }
        }
    }


    public int countEdgeIntersections() {
        int intersectionCount = 0;

        // Iterate over each pair of edges
        for (int i = 0; i < edges.size(); i++) {
            for (int j = i + 1; j < edges.size(); j++) {
                Edge edge1 = edges.get(i);
                Edge edge2 = edges.get(j);

                // Check if the two edges intersect
                if (doEdgesIntersect(edge1, edge2)) {
                    intersectionCount++;
                }
            }
        }

        return intersectionCount;
    }


    private boolean doEdgesIntersect(Edge e1, Edge e2) {
        PointInterface p1 = e1.getStart();
        PointInterface p2 = e1.getEnd();
        PointInterface q1 = e2.getStart();
        PointInterface q2 = e2.getEnd();

        if (p1.getId().equals(q1.getId())||p1.getId().equals(q2.getId())||p2.getId().equals(q1.getId())||p2.getId().equals(q2.getId())){
            return false;
        }
        // Extract coordinates of the points
        int[] p1Coords = p1.getCoordinates();
        int[] p2Coords = p2.getCoordinates();
        int[] q1Coords = q1.getCoordinates();
        int[] q2Coords = q2.getCoordinates();

        
    
        // Coordinates of points
        int p1x = p1Coords[0];
        int p1y = p1Coords[1];
        int p2x = p2Coords[0];
        int p2y = p2Coords[1];
        int q1x = q1Coords[0];
        int q1y = q1Coords[1];
        int q2x = q2Coords[0];
        int q2y = q2Coords[1];
    
        // Determine orientations using cross product
        int o1 = orientation(p1x, p1y, p2x, p2y, q1x, q1y);
        int o2 = orientation(p1x, p1y, p2x, p2y, q2x, q2y);
        int o3 = orientation(q1x, q1y, q2x, q2y, p1x, p1y);
        int o4 = orientation(q1x, q1y, q2x, q2y, p2x, p2y);
    
        // General case
        if (o1 != o2 && o3 != o4) {
            return true;
        }
    
        // Special cases
        // p1, p2, q1 are collinear and q1 lies on p1p2
        if (o1 == 0 && onSegment(p1x, p1y, q1x, q1y, p2x, p2y)) {
            return true;
        }
    
        // p1, p2, q2 are collinear and q2 lies on p1p2
        if (o2 == 0 && onSegment(p1x, p1y, q2x, q2y, p2x, p2y)) {
            return true;
        }
    
        // q1, q2, p1 are collinear and p1 lies on q1q2
        if (o3 == 0 && onSegment(q1x, q1y, p1x, p1y, q2x, q2y)) {
            return true;
        }
    
        // q1, q2, p2 are collinear and p2 lies on q1q2
        if (o4 == 0 && onSegment(q1x, q1y, p2x, p2y, q2x, q2y)) {
            return true;
        }
    
        // No intersection
        return false;
    }
    
    // Helper function to calculate orientation
    private int orientation(int p1x, int p1y, int p2x, int p2y, int qx, int qy) {
        int val = (qy - p1y) * (p2x - p1x) - (qx - p1x) * (p2y - p1y);
        if (val == 0) {
            return 0; // Collinear
        } else if (val > 0) {
            return 1; // Clockwise
        } else {
            return -1; // Counterclockwise
        }
    }
    
    // Helper function to check if point q lies on line segment p-r
    private boolean onSegment(int px, int py, int qx, int qy, int rx, int ry) {
        if (qx <= Math.max(px, rx) && qx >= Math.min(px, rx) &&
            qy <= Math.max(py, ry) && qy >= Math.min(py, ry)) {
            return true;
        }
        return false;
    }
    



    // UNUSED
    public void deleteDuplicateEdges() {
        List<Edge> uniqueEdges = new ArrayList<>();
        for (Edge edge : edges) {
            boolean isDuplicate = false;
            for (Edge uniqueEdge : uniqueEdges) {
                if (edge.isReverseOf(uniqueEdge)) {
                    isDuplicate = true;
                    break;
                }
            }
            if (!isDuplicate && !edge.startAndEndAtSamePoint()) {
                uniqueEdges.add(edge);
            }
        }
        edges = uniqueEdges;
    }

    // UNUSED
    boolean doesNodeExists(String voisin) {
        int i = 0;
        while(i<nodes.size()){
            if (nodes.get(i).getId().equals(voisin)){
                return true;
            }
            i++;
        }
        return false;
    }


    // UNUSED
    public boolean doesEdgeExist(Edge input) {
        for (Edge edge : edges) {
            if (edge.isReverseOf(input) || edge.isSameAs(input)) {
                return true;
            }
        }
        return false;
    }


    public String printEdgesAndNodes(){
        String result = " Edges: ";
        for(Edge edge : edges){
            result = result + "("+edge.getStart().getId()+","+edge.getEnd().getId()+") ";
        }
        result = result + "\n";
        for (T node : nodes){
            result = result+node.getId()+" ";
        }
        return result;
    }


    // COPY GRAPH
    // Method to generate an independent copy of the graph
    public Graph<T> copy() {

        List<T> copiedNodes = new ArrayList<>();
        List<Edge> copiedEdges = new ArrayList<>();

        // Copy nodes
        for (T node : this.nodes) {
            copiedNodes.add((T) node.copy()); // Assuming T implements copy method
        }

        // Copy edges
        for (Edge edge : this.edges) {
            // Assuming Edge constructor copies nodes properly
            copiedEdges.add(new Edge(edge.getStart().copy(), edge.getEnd().copy()));
        }

        return new Graph<>(copiedNodes, copiedEdges);
    }



    public Graph<T> copyNodes() {

        List<T> copiedNodes = new ArrayList<>();
        List<Edge> copiedEdges = new ArrayList<>();

        // Copy nodes
        for (T node : this.nodes) {
            copiedNodes.add((T) node.copy()); // Assuming T implements copy method
        }

        return new Graph<>(copiedNodes, copiedEdges);
    }
}
