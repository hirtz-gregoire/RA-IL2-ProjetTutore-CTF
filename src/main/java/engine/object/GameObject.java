package engine.object;

import engine.Coordinate;

public abstract class GameObject {

    protected Coordinate coordinate;
    protected double radius;

    public GameObject(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }
    public double getRadius() {return radius;}
}
