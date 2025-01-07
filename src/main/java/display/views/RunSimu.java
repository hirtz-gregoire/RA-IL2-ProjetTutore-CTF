package display.views;

import display.Display;
import display.controllers.Controller;
import display.model.ModelMVC;
import engine.Coordinate;
import engine.Engine;
import engine.Team;
import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.GameObject;
import ia.model.DecisionTree;
import ia.model.Random;
import ia.model.TestRaycast;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RunSimu extends View {

    private final Display display;
    private final Engine engine;
    private Thread gameThread;

    public RunSimu(ModelMVC modelMVC, Controller controller) throws IOException {
        super(modelMVC);
        this.pane = loadFxml("RunSimu", this.model);

        GameMap map = GameMap.loadFile("ressources/maps/open_space.txt");
        List<GameObject> objects = map.getGameObjects();

        Pane pane = (Pane)this.pane.lookup("#root");

        Label[] labels = new Label[1];
        labels[0] = new Label("Test 000");

        display = new Display(pane, map, 1024, new Label("Test1"), labels, labels);

        List<Agent> agents = new ArrayList<>();
        agents.add(
                new Agent(
                        new Coordinate(0, 0),
                        0.35,
                        1,
                        0.5,
                        180,
                        Team.RED,
                        Optional.empty(),
                        new DecisionTree()
                ));
        agents.add(
                new Agent(
                        new Coordinate(0, 0),
                        0.35,
                        1,
                        0.5,
                        180,
                        Team.BLUE,
                        Optional.empty(),
                        new TestRaycast()
                ));

        engine = new Engine(2, agents, map, objects, display, 10, 1.5, 123456L);

        Task<Void> gameTask = new Task<>() {
            @Override
            protected Void call() {
                engine.run();
                return null;
            }
        };
        gameThread = new Thread(gameTask);
        gameThread.setDaemon(true); // Stop thread when exiting
        gameThread.start();
        gameThread.interrupt();
    }

    @Override
    protected void update() {
        super.update();
    }
}
