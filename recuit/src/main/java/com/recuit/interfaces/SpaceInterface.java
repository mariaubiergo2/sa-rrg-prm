package com.recuit.interfaces;

import com.recuit.interfaces.PointInterface;


public interface SpaceInterface <T extends PointInterface> {

    abstract double lebesgueMeasure();
    abstract double unitBall();
    abstract boolean areConnectable(T p1, T p2);
    abstract T randomPoint();

    abstract T randomFreePoint();
    abstract boolean isAvailable(T p);
    boolean existsPoint(T p);
    void setPointsNeighbors(int radius);

}
