package ia.perception;

import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.GameObject;

import java.util.List;

public abstract class Perception {
    protected Agent my_agent;
    public Perception(Agent a){
        my_agent = a;
    }

    public abstract List<PerceptionValue> getValue(GameMap map, List<Agent> agents, List<GameObject> gameObjects);

    public Agent getMy_agent() {
        return my_agent;
    }
    public void setMy_agent(Agent my_agent) {
        this.my_agent = my_agent;
    }
}
