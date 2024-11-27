package engine.map;

import engine.Team;

/**
 * Classe représentant un mur
 */
public class Wall extends Cell {
    public Wall(Team team) {
        super.isWalkable = false;
        super.team = team;
    }
}
