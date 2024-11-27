package engine.map;


import engine.Team;

/**
 * Cell, or tile of the map
 */
public abstract class Cell {

    /** Define whether the tile os walkable */
    protected boolean isWalkable;

    /**
     * Team of this cell
     */
    protected Team team;

    public boolean isWalkable() {
        return isWalkable;
    }

    /**
     * @return The team of this cell
     */
    public Team getTeam() {return team;}
}
