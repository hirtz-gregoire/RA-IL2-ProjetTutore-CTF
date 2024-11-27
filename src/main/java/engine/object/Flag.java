package engine.object;

import engine.Coordinate;
import engine.Team;

public class Flag extends GameObject {

    protected Team team;

    public Flag(Coordinate coordinate, Team team) {
        super(coordinate);
        this.team = team;
    }
}
