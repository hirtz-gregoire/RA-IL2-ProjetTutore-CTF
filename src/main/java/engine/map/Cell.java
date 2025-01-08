package engine.map;

import engine.Vector2;
import engine.Team;

/**
 * Cell, or tile of the map
 */
public abstract class Cell {

    /** Define whether the tile is walkable */
    protected boolean isWalkable;
    protected Vector2 coordinate;
    //Team of this cell
    protected Team team;

    protected Cell(Vector2 coordinate, Team team) {
        this.coordinate = coordinate;
        this.team = team;
    }

    public boolean isWalkable() {
        return isWalkable;
    }

    /**
     * @return The team of this cell
     */
    public Team getTeam() {return team;}
    public Vector2 getCoordinate() {return coordinate;}
}
