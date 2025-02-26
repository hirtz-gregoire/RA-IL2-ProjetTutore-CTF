package engine.object;

import engine.Team;
import engine.Vector2;

public abstract class GameObject {

    protected Vector2 coordinate;
    protected Vector2 spawnCoordinate;
    protected double radius;
    protected Team team;

    public GameObject(Vector2 coordinate, Team team) {
        this.spawnCoordinate = coordinate;
        this.coordinate = coordinate;
        this.team = team;
    }

    public Vector2 getCoordinate() {
        return coordinate;
    }
    public Vector2 getSpawnCoordinate() {return spawnCoordinate;}
    public void setCoordinate(Vector2 coordinate) {
        this.coordinate = coordinate;
    }
    public double getRadius() {return radius;}
    public Team getTeam() {
        return team;
    }
    public void setTeam(Team team) {
        this.team = team;
    }

    @Override
    public String toString() {
        return "GameObject{" +
                "coordinate=" + coordinate +
                ", spawnCoordinate=" + spawnCoordinate +
                ", team=" + team +
                '}';
    }

    public abstract GameObject copy();
}
