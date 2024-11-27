package engine.map;

import engine.Coordinate;
import engine.Team;

/**
 * Define a walkable floor cell
 */
public class Ground extends Cell {
    public Ground(Coordinate coordinate, Team team) {
        super(coordinate, team);
        super.isWalkable = true;
    }
}
