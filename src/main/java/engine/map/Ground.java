package engine.map;

import engine.Vector2;
import engine.Team;

import java.util.HashMap;

/**
 * Define a walkable floor cell
 */
public class Ground extends Cell {
    /**
     * Ground constructor, owned by the team given in param
     * @param team The team owning this ground cell
     */
    public Ground(Vector2 coordinate, Team team) {
        super(coordinate, team);
        super.isWalkable = true;
        super.team = team;
    }

    @Override
    public Ground copy() {
        Ground cell = new Ground(coordinate.copy(), team);
        cell.bakedFlagDistances = new HashMap<>(this.bakedFlagDistances);
        cell.bakedTerritoryDistances = new HashMap<>(this.bakedTerritoryDistances);
        return cell;
    }
}
