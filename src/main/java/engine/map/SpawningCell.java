package engine.map;

import engine.Vector2;
import engine.Team;

import java.util.HashMap;

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
    public SpawningCell copy() {
        SpawningCell cell = new SpawningCell(coordinate.copy(), team);
        cell.bakedFlagDistances = new HashMap<>(this.bakedFlagDistances);
        cell.bakedTerritoryDistances = new HashMap<>(this.bakedTerritoryDistances);
        return cell;
    }
}
