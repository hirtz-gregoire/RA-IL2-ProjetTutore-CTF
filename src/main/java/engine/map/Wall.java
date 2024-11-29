package engine.map;

import engine.Coordinate;
import engine.Team;

/**
 * Classe représentant un mur
 */
public class Wall extends Cell {
    public Wall(Coordinate coordinate, Team team) {
        super(coordinate, team);
        super.isWalkable = false;
    }
}
