package engine.object;

import engine.Coordinate;
import engine.Team;

public class Flag extends GameObject {

    protected Team team;
    private boolean isHolded;

    public Flag(Coordinate coordinate, Team team) {
        super(coordinate);
        this.team = team;
        this.isHolded = false;
    }

    public Team getTeam() {
        return team;
    }
    public boolean getHolded() {
        return this.isHolded;
    }
    public void setHolded(boolean holded) {
        isHolded = holded;
    }

}
