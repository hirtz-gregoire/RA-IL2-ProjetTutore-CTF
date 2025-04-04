package ia.perception;

import engine.agent.Agent;

public abstract class Compass extends Perception {
    protected Filter filter;

    public Compass(Agent a, Filter filter) {
        super(a);
        this.filter = filter;
    }

    public void setTeamMode(Filter.TeamMode mode) {
        filter.setTeamMode(mode);
    }
    public Filter.TeamMode getTeamMode() {
        return filter.getTeamMode();
    }
    public Filter.DistanceMode getDistanceMode() {
        return filter.getDistanceMode();
    }

    @Override
    public String toString() {
        return this.getClass().getName()+";"+filter.getTeamMode()+";"+filter.getDistanceMode();
    }
}
