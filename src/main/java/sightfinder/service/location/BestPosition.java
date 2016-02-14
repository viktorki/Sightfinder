package sightfinder.service.location;

import sightfinder.model.Landmark;

/**
 * Created by krasimira on 13.02.16.
 */
public class BestPosition implements Comparable<BestPosition> {

    private double length;
    private Landmark comesFrom;

    public BestPosition(double length, Landmark comesFrom) {
        this.length = length;
        this.comesFrom = comesFrom;
    }

    @Override
    public int compareTo(BestPosition o) {
        return Double.compare(length, o.length);
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public Landmark getComesFrom() {
        return comesFrom;
    }

    public void setComesFrom(Landmark comesFrom) {
        this.comesFrom = comesFrom;
    }
}
