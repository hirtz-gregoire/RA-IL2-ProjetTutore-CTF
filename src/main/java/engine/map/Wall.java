package engine.map;

import engine.Vector2;
import engine.Team;

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
        return new Wall(coordinate.copy(), team);
    }
}
