package engine.map;

import engine.Coordinate;
import engine.Team;

/**
 * Define a cell where any agent of the team can spawn
 */
public class SpawningCell extends Cell {
    public SpawningCell(Coordinate coordinate, Team team) {
        super(coordinate, team);
        super.isWalkable = true;
    }
}
