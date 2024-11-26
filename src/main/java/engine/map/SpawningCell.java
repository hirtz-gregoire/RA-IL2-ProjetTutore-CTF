package engine.map;

import engine.Team;

/**
 * Define a cell where any agent of the team can spawn
 */
public class SpawningCell extends Cell {
    /**
     * Team of this cell
     */
    private Team team;

    public SpawningCell(Team team) {
        this.team = team;
    }

    public Team getTeam() {
        return team;
    }
}
