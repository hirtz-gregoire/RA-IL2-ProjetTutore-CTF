package engine.map;

import engine.Coordinate;
import engine.Team;

/**
 * Cell, or tile of the map
 */
public abstract class Cell {

    /** Define whether the tile is walkable */
    protected boolean isWalkable;
    protected Coordinate coordinate;
    //Team of this cell
    protected Team team;

    protected Cell(Coordinate coordinate, Team team) {
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
    public Coordinate getCoordinate() {return coordinate;}
}
