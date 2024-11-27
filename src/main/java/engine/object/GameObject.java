package engine.object;

import engine.Coordinate;

public abstract class GameObject {

    protected Coordinate coordinate;

    public GameObject(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }
}
