package engine.map;

import engine.Team;

/**
 * Define a cell where any agent of the team can spawn
 */
public class SpawningCell extends Cell {
    public SpawningCell(Team team) {
        super.isWalkable = true;
        super.team = team;
    }
}
