package display;

import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.GameObject;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;

import java.util.List;

public abstract class Display {

    public Pane root = null;

    public Display(Pane node){
        this.root = node;
    }

    public abstract void update(GameMap map, List<Agent> agents, List<GameObject> objects);
}
