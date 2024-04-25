package com.recuit;

import java.util.*;
import com.recuit.interfaces.PointInterface;

// Represents a graph composed of nodes and edges
public class Graph<T extends PointInterface> {
    
    List<T> nodes; // List of nodes in the graph
    List<Edge> edges; // List of edges in the graph

    // Constructor to initialize an empty graph
    public Graph() {
        this.nodes = new ArrayList<>();
        this.edges = new ArrayList<>();
    }

    // Constructor to initialize a graph with provided nodes and edges
    public Graph(List<T> nodes, List<Edge> edges) {
        this.nodes = nodes;
        this.edges = edges;
    }

    // Method to get the list of edges in the graph
    public List<Edge> getEdges() {
        return this.edges;
    }

    // Method to print information about the graph
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

    public double evaluateMe() {
        // double maxEdge = computeMaxDistanceToABase();
        double maxEdge = 0.0;
        double penalty = 90000;
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
            if (edge.distance()>maxDistance){
                if (bases.contains(edge.getStart().getId()) || bases.contains(edge.getEnd().getId())){
                    maxDistance=edgeDist;
                }
                
            }
        }
        return maxDistance;
    }
    
    // Second cost function
    // Method to evaluate the graph based on connectivity and total distance
    public double evaluateMeConnectivityAndDistance() {
        double maxEdge = computeMaxEdgeDistance();

        double penalty = 90000;
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
    public double evaluateMeCF1() {
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

    // Method to delete duplicate edges from the graph
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
