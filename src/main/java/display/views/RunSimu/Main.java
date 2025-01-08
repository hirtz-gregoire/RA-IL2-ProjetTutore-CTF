package display.views.RunSimu;

import display.Display;
import display.model.ModelMVC;
import display.model.RunSimuModel;
import display.views.View;
import engine.Coordinate;
import engine.Engine;
import engine.Team;
import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.GameObject;
import ia.model.DecisionTree;
import ia.model.ModelEnum;
import ia.model.Random;
import ia.model.TestRaycast;
import javafx.concurrent.Task;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Main extends View {

    private final Display display;
    private final Engine engine;
    private Thread gameThread;

    public Main(ModelMVC modelMVC) throws IOException {
        super(modelMVC);
        this.pane = loadFxml("RunSimu/Main", this.modelMVC);

        RunSimuModel model = (RunSimuModel)this.modelMVC;

        //GameMap map = GameMap.loadFile("ressources/maps/open_space.txt");
        GameMap map = model.getMap();
        List<GameObject> objects = map.getGameObjects();

        Pane pane = (Pane)this.pane.lookup("#root");

        display = new Display(pane, map, 1024);
        ((RunSimuModel)modelMVC).setDisplay(display);

        List<Agent> agents = new ArrayList<>();
        for (int i=0; i<map.getTeams().size(); i++){
            for (int j=0; j<model.getNbPlayers(); j++){
                var agent = new Agent(
                        new Coordinate(0, 0),
                        0.35,
                        model.getSpeedPlayers(),
                        model.getSpeedPlayers()/2,
                        180,
                        map.getTeams().get(i),
                        Optional.empty(),
                        ModelEnum.getClass(model.getModelList().get(i).getFirst())
                );
                agents.add(agent);
            }
        }

        engine = new Engine(map.getNbEquipes(), agents, map, objects, display, model.getRespawnTime(), 1.5, 123456L);
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

        // syncho checkbox par rapport à valeur du model
        CheckBox checkBox = (CheckBox)this.pane.lookup("#boxColl");
        checkBox.setSelected(display.isShowBoxCollisions());

        checkBox = (CheckBox)this.pane.lookup("#boxPerc");
        checkBox.setSelected(display.isShowPerceptions());

        // maj du tps cible selon valeur de model.saveTps
        Label tps = (Label)this.pane.lookup("#tps");
        RunSimuModel model = (RunSimuModel) modelMVC;

        tps.setText(String.valueOf(model.getSaveTps()));

    }
}
