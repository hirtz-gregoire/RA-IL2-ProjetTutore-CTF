package engine.map;

import engine.Vector2;
import engine.Team;

/**
 * Define a walkable floor cell
 */
public class Ground extends Cell {
    /**
     * Ground constructor, owned by the team given in param
     * @param team The team owning this ground cell
     */
    public Ground(Vector2 coordinate, Team team) {
        super(coordinate, team);
        super.isWalkable = true;
        super.team = team;
    }

    @Override
    public Ground copy() {
        return new Ground(coordinate.copy(), team);
    }
}
