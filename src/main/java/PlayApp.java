import engine.Engine;
import engine.agent.Agent;
import engine.map.Map;
import engine.object.Object;

import java.util.List;

public class PlayApp {

    public static void main(String[] args) {

        List<Agent> agents = null;
        Map map = null;
        List<Object> objects = null;

        Engine engine = new Engine(agents, map, objects);
        engine.run();
    }
}
