package engine.map;

import engine.Vector2;
import engine.Team;

import java.util.HashMap;

/**
 * Classe représentant un mur
 */
public class Wall extends Cell {
    public Wall(Vector2 coordinate, Team team) {
        super(coordinate, team);
        super.isWalkable = false;
    }

    @Override
    public Wall copy() {
        Wall wall = new Wall(getCoordinate().copy(), team);
        wall.bakedFlagDistances = new HashMap<>(this.bakedFlagDistances);
        wall.bakedTerritoryDistances = new HashMap<>(this.bakedTerritoryDistances);
        return wall;
    }
}
