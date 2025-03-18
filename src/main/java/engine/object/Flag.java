package engine.object;

import engine.Vector2;
import engine.Team;

import java.util.Objects;

public class Flag extends GameObject {

    private boolean isHolded;

    public Flag(Vector2 coordinate, Team team) {
        super(coordinate, team);
        this.isHolded = false;
        this.radius = 0.5f;
    }

    public Team getTeam() {
        return this.team;
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

    @Override
    public Flag copy() {
        return new Flag(this.coordinate.copy(), this.team);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Flag flag) {
            if(this.spawnCoordinate.x() != flag.spawnCoordinate.x()) return false;
            if(this.spawnCoordinate.y() != flag.spawnCoordinate.y()) return false;
            if(!this.team.equals(flag.team)) return false;
            return this.radius == flag.radius;
        }
        return false;
    }

    private final int preComputedHash = Objects.hash(spawnCoordinate, team, radius);
    @Override
    public int hashCode() {
        return preComputedHash;
    }
}
