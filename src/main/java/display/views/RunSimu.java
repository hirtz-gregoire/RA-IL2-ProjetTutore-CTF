package display.views;

import display.Display;
import display.model.ModelMVC;
import display.model.RunSimuModel;
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
import javafx.scene.control.CheckBox;
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

    public RunSimu(ModelMVC modelMVC) throws IOException {
        super(modelMVC);
        this.pane = loadFxml("RunSimu", this.modelMVC);


        GameMap map = GameMap.loadFile("ressources/maps/dust.txt");
        List<GameObject> objects = map.getGameObjects();

        Pane pane = (Pane)this.pane.lookup("#root");

        display = new Display(pane, map, 1024);
        ((RunSimuModel)modelMVC).setDisplay(display);

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
                        Team.RED,
                        Optional.empty(),
                        new Random()
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
                        new Random()
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
        ((RunSimuModel)modelMVC).setEngine(engine);

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

        this.update();
    }

    @Override
    public void update() {
        super.update();

        // syncho checkbox par rapport a valeur du model
        CheckBox checkBox = (CheckBox)this.pane.lookup("#boxColl");
        checkBox.setSelected(display.isShowBoxCollisions());

        // maj du tps cible selon valeur de model.saveTps
        Label tps = (Label)this.pane.lookup("#tps");
        RunSimuModel model = (RunSimuModel) modelMVC;

        tps.setText(String.valueOf(model.getSaveTps()));

    }
}
