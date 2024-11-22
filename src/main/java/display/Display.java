package display;

import engine.agent.Agent;
import engine.map.Map;
import engine.object.Object;

import java.util.List;

public interface Display {

    void update(Map map, List<Agent> agents, List<Object> objects);

}
