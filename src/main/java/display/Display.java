package display;

import engine.agent.Agent;
import engine.map.Map;
import engine.object.Object;
import javafx.scene.Node;

import java.util.List;

public abstract class Display {

    public Node root = null;

    public Display(Node node){}

    abstract void update(Map map, List<Agent> agents, List<Object> objects);

}
