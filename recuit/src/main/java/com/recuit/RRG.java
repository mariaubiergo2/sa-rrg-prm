package com.recuit;

import java.util.ArrayList;
import java.util.List;

import com.recuit.interfaces.PointInterface;
import com.recuit.interfaces.SpaceInterface;

public class RRG <T extends PointInterface> {

    private List<T> nodes;
    private double minRadius;
    private double etha;

    public RRG(){
        
    }
    
    public Graph<T> createGraph(List<T> nodes, SpaceInterface<T> space, double alpha, double etha, int n){
        /*
         * List<Point2D> nodes: the Point2D list of the current state
         * SpaceInterface<Point2D> space: is the grid
         * alpha 1.1
         * etha=f(space, generated points)
         * n: the maximum number of new nodes that you want?
         */

        this.nodes = nodes;
        this.etha = 6;

        alpha = 1.1;

        minRadius = 2*Math.pow(1+1/2.,1/2.)*alpha*Math.pow(space.lebesgueMeasure()/Math.PI, 1/2.);

        List<Edge> edges = new ArrayList<>();
        for (int i=0; i<n; i++){
            T randomPoint = space.randomFreePoint(); // Changed to a free point!
            T nearest = nearest(randomPoint);
            T newPoint = steer(randomPoint, nearest);
            if(space.areConnectable(nearest, newPoint)){
                List<T> near = near(newPoint);
                nodes.add(newPoint);
                edges.add(new Edge(nearest, newPoint));
                edges.add(new Edge(newPoint, nearest));
                for (T point : near) {
                    if(space.areConnectable(point, newPoint)){
                        edges.add(new Edge(point, newPoint));
                        edges.add(new Edge(newPoint, point));
                    }
                }
            }
        }
        return new Graph<T>(nodes,edges);
    }


    public T nearest(T point){
        double minDistance = Double.MAX_VALUE;
        T bestPoint = null;
        for (T point2 : nodes) {
                double distance = point.distanceTo(point2);
                if(distance<minDistance){
                minDistance = distance;
                bestPoint = point2;
                }
        }
        return bestPoint;
    }

    public List<T> near(T point){
        List<T> near = new ArrayList<>();
        double n = nodes.size();
        double radius = Math.min(Math.pow(Math.log(n)/n,1/2.)*minRadius,etha);
        for(T node: nodes){
            double distance = node.distanceTo(node);
            if(distance<=radius){
                near.add(node);
            }
        }
        return near;
    }


    @SuppressWarnings("unchecked")
    public T steer(T point,T nearestPoint){
        double distance = point.distanceTo(nearestPoint);
        if(distance<etha){
            return point;
        }
        else{
            double angle = nearestPoint.angle(point);
            PointInterface newPoint = nearestPoint.newPoint(etha, angle);
            return (T)newPoint;
            

        }
        
    }
}
