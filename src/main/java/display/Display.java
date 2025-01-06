package display;

import engine.Engine;
import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.GameObject;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;


import java.util.List;

public class Display {

    private Pane root;

    public Display(Pane simulationBox, GameMap map, int taille, Label labelTpsActualEngine, Label[] labelsNbJoueursMorts, Label[] labelsTempsProchaineReaparition) {
        this.root = simulationBox;
    }

    public void update(Engine engine, GameMap map, List<Agent> agents, List<GameObject> objects) {

    }
}
