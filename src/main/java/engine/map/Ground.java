package engine.map;

import engine.Team;

/**
 * Define a walkable floor cell
 */
public class Ground extends Cell {
    /** Team of this cell */
    private Team team;

    /**
     * Ground constructor, owned by the team given in param
     * @param team The team owning this ground cell
     */
    public Ground(Team team) {
        super.isWalkable = true;
        super.team = team;
    }
/**
     * @return The team of this cell
     */
    public Team getTeam() {
        return team;
    }
}
