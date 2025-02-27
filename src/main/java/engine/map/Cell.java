package engine.map;

import engine.Vector2;
import engine.Team;
import engine.object.GameObject;

/**
 * Cell, or tile of the map
 */
public abstract class Cell extends GameObject {

    /** Define whether the tile is walkable */
    protected boolean isWalkable;

    protected Cell(Vector2 coordinate, Team team) {
        super(coordinate, team);
    }

    public boolean isWalkable() {
        return isWalkable;
    }

    public abstract Cell copy();
}
