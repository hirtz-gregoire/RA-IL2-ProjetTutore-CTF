package engine.object;

import engine.Vector2;

public abstract class GameObject {

    protected Vector2 coordinate;
    protected double radius;

    public GameObject(Vector2 coordinate) {
        this.coordinate = coordinate;
    }

    public Vector2 getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Vector2 coordinate) {
        this.coordinate = coordinate;
    }
    public double getRadius() {return radius;}
}
