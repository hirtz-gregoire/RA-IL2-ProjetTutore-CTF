package ia.perception;

import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.GameObject;

import java.util.List;

public abstract class Perception {
    private Agent my_agent;
    public Perception(Agent a){
        my_agent = a;
    }
    public abstract PerceptionValue getValue(GameMap map, List<Agent> agents, GameObject go);

    public Agent getMy_agent() {
        return my_agent;
    }

    public void setMy_agent(Agent my_agent) {
        this.my_agent = my_agent;
    }
}
