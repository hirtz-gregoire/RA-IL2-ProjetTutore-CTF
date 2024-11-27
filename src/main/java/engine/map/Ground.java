package engine.map;

import engine.Team;

/**
 * Define a walkable floor cell
 */
public class Ground extends Cell {
    public Ground(Team team) {
        super.isWalkable = true;
        super.team = team;
    }
}
