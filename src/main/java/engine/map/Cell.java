package engine.map;


import engine.Team;

/**
 * Cellule, ou case du plateau.
 */
public abstract class Cell {

    /**
     * Définis si les joueurs peuvent se placer sur la case
     */
    protected boolean isWalkable;

    /**
     * Team of this cell
     */
    protected Team team;

    public boolean isWalkable() {
        return isWalkable;
    }

    public Team getTeam() {return team;}
}
