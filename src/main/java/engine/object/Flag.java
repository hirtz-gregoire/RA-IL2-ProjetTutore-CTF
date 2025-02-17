package engine.object;

import engine.Vector2;
import engine.Team;

public class Flag extends GameObject {

    protected Team team;
    private boolean isHolded;

    public Flag(Vector2 coordinate, Team team) {
        super(coordinate);
        this.team = team;
        this.isHolded = false;
        this.radius = 0.5f;
    }

    public Team getTeam() {
        return team;
    }
    public void setTeam(Team team) {
        this.team = team;
    }
    public boolean getHolded() {
        return this.isHolded;
    }
    public void setHolded(boolean holded) {
        isHolded = holded;
    }
}
