package engine.map;

import engine.Vector2;
import engine.Team;

/**
 * Define a cell where any agent of the team can spawn
 */
public class SpawningCell extends Cell {

    /**
     * Ground constructor, owned by the team given in param
     * @param team The team owning this ground cell
     */
    public SpawningCell(Vector2 coordinate, Team team) {
        super(coordinate, team);
        super.isWalkable = true;
        super.team = team;
    }

    @Override
    public Cell copy() {
        return new SpawningCell(coordinate.copy(), team);
    }
}
