package engine.map;


import engine.Team;

/**
 * Cell, or tile of the map
 */
public abstract class Cell {

    /** Define whether the tile os walkable */
    private boolean isWalkable;

    /**
     * Team of this cell
     */
    protected Team team;

    public boolean isWalkable() {
        return isWalkable;
    }

    public Team getTeam() {return team;}
}
