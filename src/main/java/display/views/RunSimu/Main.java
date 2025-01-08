package display.views.RunSimu;

import display.Display;
import display.model.ModelMVC;
import display.model.RunSimuModel;
import display.views.View;
import engine.Engine;
import engine.Team;
import engine.Vector2;
import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.GameObject;
import ia.model.DecisionTree;
import ia.model.TestRaycast;
import ia.perception.PerceptionType;
import javafx.concurrent.Task;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.*;

public class Main extends View {

    private final Display display;
    private final Engine engine;
    private Thread gameThread;

    public Main(ModelMVC modelMVC) throws IOException {
        super(modelMVC);
        this.pane = loadFxml("RunSimu/Main", this.modelMVC);


        GameMap map = GameMap.loadFile("ressources/maps/dust.txt");
        List<GameObject> objects = map.getGameObjects();

        Pane pane = (Pane)this.pane.lookup("#root");

        TreeView<CheckBoxTreeItem> treeView = (TreeView) this.pane.lookup("#tvDisplay");
        treeView.setCellFactory(_ -> new CheckBoxTreeCell<>());
        TreeItem<CheckBoxTreeItem> invisibleRoot = treeView.getRoot();
        TreeItem<CheckBoxTreeItem> rootHitbox = invisibleRoot.getChildren().get(0);
        TreeItem<CheckBoxTreeItem> rootPerception = invisibleRoot.getChildren().get(1);

        Map<PerceptionType, Boolean> desiredPerceptions = new HashMap<>();
        for(PerceptionType type : PerceptionType.values()) {
            desiredPerceptions.put(type, Boolean.FALSE);
        }

        display = new Display(pane, map, 1024, desiredPerceptions);
        ((RunSimuModel)modelMVC).setDisplay(display);

        List<Agent> agents = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            agents.add(
                    new Agent(
                            new Vector2(0, 0),
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
                            new Vector2(0, 0),
                            0.35,
                            1,
                            0.5,
                            180,
                            Team.BLUE,
                            Optional.empty(),
                            new TestRaycast()
                    ));
        }
        //((PerceptionRaycast)agents.getFirst().getModel().getPerceptions().getFirst()).setRayCount(2);

        engine = new Engine(2, agents, map, objects, display, 10, 1.5, 123456L);
        ((RunSimuModel)modelMVC).setEngine(engine);

        for(PerceptionType type : PerceptionType.values()) {
            CheckBoxTreeItem checkBoxTreeItem = new CheckBoxTreeItem(type.toString());
            rootPerception.getChildren().add(checkBoxTreeItem);
            checkBoxTreeItem.selectedProperty().addListener((_, _, newValue) -> {
                desiredPerceptions.put(type, newValue);
                display.update(engine, map, agents, objects);
            });
        }

        CheckBoxTreeItem checkBoxHitbox = (CheckBoxTreeItem) rootHitbox;
        checkBoxHitbox.selectedProperty().addListener((_, _, _) -> display.switchShowBoxCollisions());

        Task<Void> gameTask = new Task<>() {
            @Override
            protected Void call() {
                engine.run();
                return null;
            }
            @Override
            protected void failed() {
                System.out.println("Error in engine thread :");
                getException().printStackTrace();
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

        // maj du tps cible selon valeur de model.saveTps
        Label tps = (Label)this.pane.lookup("#tps");
        RunSimuModel model = (RunSimuModel) modelMVC;

        tps.setText(String.valueOf(model.getSaveTps()));

    }
}
