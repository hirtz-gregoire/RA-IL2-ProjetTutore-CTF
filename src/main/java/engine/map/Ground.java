package engine.map;

import engine.Team;

/**
 * Define a walkable floor cell
 */
public class Ground extends Cell {
    /**
     * Team of this cell
     */
    private Team team;

    public Ground(Team team) {
        this.team = team;
    }

    public Team getTeam() {
        return team;
    }
}
