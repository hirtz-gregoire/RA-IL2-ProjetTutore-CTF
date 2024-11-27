package engine.map;

import engine.Team;

/**
 * Define a walkable floor cell
 */
public class Ground extends Cell {
    /**
     * Ground constructor, owned by the team given in param
     * @param team The team owning this ground cell
     */
    public Ground(Team team) {
        super.isWalkable = true;
        super.team = team;
    }
}
