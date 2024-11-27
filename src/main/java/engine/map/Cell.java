package engine.map;


import engine.Coordinate;
import engine.Team;

/**
 * Cellule, ou case du plateau.
 */
public abstract class Cell {

    /**
     * Définis si les joueurs peuvent se placer sur la case
     */
    protected boolean isWalkable;
    protected Coordinate coordinate;

    /**
     * Team of this cell
     */
    protected Team team;

    protected Cell(Coordinate coordinate, Team team) {
        this.coordinate = coordinate;
        this.team = team;
    }

    public boolean isWalkable() {
        return isWalkable;
    }

    public Team getTeam() {return team;}
    public Coordinate getCoordinate() {return coordinate;}
}
