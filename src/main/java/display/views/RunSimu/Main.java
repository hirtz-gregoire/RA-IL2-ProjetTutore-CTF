package display.views.RunSimu;

import display.Display;
import display.SongPlayer;
import display.model.ModelMVC;
import display.model.RunSimuModel;
import display.views.View;
import engine.Engine;
import engine.Vector2;
import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.GameObject;
import ia.model.Human;
import ia.model.Model;
import ia.model.ModelEnum;
import ia.model.NeuralNetworks.NNFileLoader;
import ia.perception.PerceptionType;
import javafx.concurrent.Task;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.io.ObjectOutput;
import java.util.*;

public class Main extends View {

    private final Display display;
    private final Engine engine;
    private Thread gameThread;

    public Main(ModelMVC modelMVC) throws IOException {
        super(modelMVC);
        this.pane = loadFxml("RunSimu/Main", this.modelMVC);

        SongPlayer.playRepeatSong("partie_simulation");

        RunSimuModel model = (RunSimuModel)this.modelMVC;

        GameMap map = model.getMap();
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
        for (int numTeam=0; numTeam<map.getTeams().size(); numTeam++){
            for (int numPlayer=0; numPlayer<model.getNbPlayers(); numPlayer++) {
                Model modelAgent;
                //Si le joueur veut jouer
                if (!Objects.equals(model.getHumanTeam().get(numTeam).get(numPlayer), "Bot"))
                    modelAgent = new Human(model.getHumanTeam().get(numTeam).get(numPlayer));
                //S'il y a un model de NN choisit
                else if (!Objects.equals(model.getNeuralNetworkTeam().get(numTeam), ""))
                    modelAgent = NNFileLoader.loadModel(model.getNeuralNetworkTeam().get(numTeam));
                else
                    modelAgent = ModelEnum.getClass(model.getModelList().get(numTeam));

                agents.add(new Agent(
                        new Vector2(0, 0),
                        0.35,
                        model.getSpeedPlayers(),
                        model.getSpeedPlayers() * 0.75,
                        180,
                        map.getTeams().get(numTeam),
                        Optional.empty(),
                        modelAgent,
                        10.0
                ));
            }
        }

        int max_turns = model.getMaxTurns();
        if(max_turns == 0){
            max_turns = Engine.INFINITE_TURN;
        }

        engine = new Engine(map.getNbEquipes(), agents, map, objects, display, model.getRespawnTime(), 1.5, model.getSeed(), max_turns);
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

        Button btnZoomOut = (Button)this.pane.lookup("#btnZoomOut");
        btnZoomOut.setOnMouseClicked(_ -> display.setScale(Math.max(display.getScale()-1,1)));
        Button btnZoomIn = (Button)this.pane.lookup("#btnZoomIn");
        btnZoomIn.setOnMouseClicked(_ -> display.setScale(Math.min(display.getScale()+1,10)));
        Button btnResetZoom = (Button)this.pane.lookup("#btnResetZoom");
        btnResetZoom.setOnMouseClicked(_ -> display.setScale(1));

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

        RunSimuModel model = (RunSimuModel) modelMVC;

        // maj du tps cible selon valeur de model.saveTps
        Label tps = (Label)this.pane.lookup("#tps");
        tps.setText(String.valueOf(model.getSaveTps()));

        //maj seed
        Label seed = (Label)this.pane.lookup("#seed");
        seed.setText(String.valueOf(model.getSeed()));
    }
}
