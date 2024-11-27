package display;

import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.GameObject;
import javafx.scene.Node;
import views.Observateur;

import java.util.List;

public abstract class Display {

    public Node root = null;

    public Display(Node node){}

    public abstract void update(GameMap map, List<Agent> agents, List<GameObject> objects);
}
