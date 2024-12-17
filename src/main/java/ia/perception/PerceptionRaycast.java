package ia.perception;

import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.GameObject;

import java.util.List;

public class PerceptionRaycast extends Perception {

    public PerceptionRaycast(Agent a) {
        super(a);
    }

    @Override
    public PerceptionValue getValue(GameMap map, List<Agent> agents, List<GameObject> go) {
        return null;
    }
}
