package com.recuit;

import java.util.ArrayList;
import java.util.List;

import com.recuit.interfaces.PointInterface;
import com.recuit.interfaces.SpaceInterface;

public class PRM<T extends PointInterface> {
    
    private static double radius;

    private static double n;

    private static double alpha;


    // Private constructor to prevent instantiation
    private PRM() {}

    public static <T extends PointInterface> Graph<T> createGraph(List<T> nodes, SpaceInterface<T> space, double alpha) {
        
        // Set only the first time
        alpha = 1.1;
        n = nodes.size();
        radius = (Math.pow(Math.log(n) / n, 1 / 2.) * 2 * Math.pow(1 + 1 / 2., 1 / 2.) * alpha * Math.pow(space.lebesgueMeasure() / space.unitBall(), 1 / 2.))/2;
        
        List<Edge> edges = new ArrayList<>();
        for (T point : nodes) {
            List<T> nearPoints = near(point, nodes);
            for (T point2 : nearPoints) {
                // edges.add(new Edge(point2, point));
                if (space.areConnectable(point2, point)) {
                    // edges.add(new Edge(point, point2));
                    edges.add(new Edge(point2, point));
                }
            }
        }

        return new Graph<T>(nodes, edges);
    }


    public static <T extends PointInterface> Graph<T> createGraphFromGraph(Graph<T> inputGraph, T newNode, T oldNode, SpaceInterface<T> space, int poisitionChanged) {
    // REVISED

        
        // System.out.println("-initial-");
        // System.out.println(inputGraph.printEdgesAndNodes());
        
        Graph oldGraph = inputGraph.copyNodes();

        oldGraph.nodes.set(poisitionChanged, newNode);

       
        for (Object edge : inputGraph.edges){
            if (((Edge) edge).connects(oldNode)){
                
            }
            else{
                oldGraph.edges.add((Edge) edge);
            }
        }
        
        List<T> nearPoints = near(newNode, oldGraph.nodes);

        for (T point : nearPoints) {
            if (space.areConnectable(point, newNode)) {
                Edge newEdge = new Edge(point, newNode);
                // if (!oldGraph.doesEdgeExist(newEdge)){
                    oldGraph.edges.add(newEdge);
                // }
                
            }
        }

        return oldGraph;
    }


    public static <T extends PointInterface> List<T> near(T point, List<T> nodes) {
        List<T> nearPoints = new ArrayList<>();

        for (T node : nodes) {
            double distance = node.distanceTo(point);
            if (distance <= radius && !point.getId().equals(node.getId())) {
            // if (distance <= radius) {
                nearPoints.add(node);
            }
        }

        return nearPoints;
    }
}
