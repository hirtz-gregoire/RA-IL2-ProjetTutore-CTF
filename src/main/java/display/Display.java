package display;

import display.renderer.AgentRenderer;
import display.renderer.GameObjectRenderer;
import engine.Engine;
import engine.Team;
import engine.agent.Agent;
import engine.map.Cell;
import engine.map.GameMap;
import engine.object.GameObject;
import ia.perception.PerceptionType;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class Display {

    private final Pane root;
    private final GridPane grid;
    private final Pane display_root;
    private Alert end_game;
    private boolean showBoxCollisions = false;
    private Map<PerceptionType,Boolean> desiredPerceptions = new HashMap<>();
    private double cellSize;
    private List<List<Cell>> cells;

    private double scale = 1;
    private double currentPosX = 0;
    private double currentPosY = 0;

    public Display(Pane pane, GameMap map, int taille, Map<PerceptionType, Boolean> desiredPerceptions) {
        this.root = pane;
        this.display_root = new Pane();
        this.desiredPerceptions = desiredPerceptions;

        end_game = new Alert(Alert.AlertType.CONFIRMATION);
        end_game.setTitle("Fin de partie");
        end_game.setHeaderText(null);

        cells = map.getCells();
        cellSize = Math.round(taille / Math.max(cells.size(), cells.getFirst().size() * 2));
        grid = new GridPane();

        root.getChildren().add(grid);
        this.root.getChildren().add(display_root);

        AtomicReference<Double> x = new AtomicReference<>((double) 0);
        AtomicReference<Double> y = new AtomicReference<>((double) 0);
        root.setOnMousePressed(event -> {
            x.set(event.getX());
            y.set(event.getY());
        });
        root.setOnMouseDragged(event -> {
            //System.out.println(x+" "+event.getX()+" - "+y+" "+event.getY());
            currentPosX = currentPosX - (x.get() - event.getX()) / 15.0;
            currentPosY = currentPosY - (y.get() - event.getY()) / 15.0;
        });

        updateGridPane();

        double maxHeight = cells.getFirst().size() * cellSize;
        double maxWidth = cells.size() * cellSize;
        root.setMaxSize(maxWidth, maxHeight);
        root.setClip(new Rectangle(maxWidth, maxHeight));
        //System.out.println(maxHeight+" "+maxWidth);
    }

    public void updateGridPane() {
        grid.getChildren().clear();
        for (int row = 0; row < cells.size(); row++) {
            for (int column = 0; column < cells.get(row).size(); column++) {
                Cell cell = cells.get(row).get(column);
                Image spriteCell = Team.getCellSprite(cell, cellSize * scale);
                ImageView imageView = new ImageView(spriteCell);
                GridPane.setConstraints(imageView, row, column);
                grid.getChildren().add(imageView);
            }
        }
    }

    private void translateSprite() {
        for(Node node : root.getChildren()) {
            node.setTranslateX(Math.clamp(currentPosX, -cellSize * (scale-1) * (cells.size()), 0));
            node.setTranslateY(Math.clamp(currentPosY, -cellSize * (scale-1) * (cells.getFirst().size()), 0));
        }
    }

    public void update(Engine engine, GameMap map, List<Agent> agents, List<GameObject> objects) {
        objects = List.copyOf(objects);
        display_root.getChildren().clear();

        // render agents
        for (Agent agent : agents) {
            AgentRenderer.render(agent, display_root, cellSize * scale, showBoxCollisions, desiredPerceptions);
        }
        // render GameObjet
        for (GameObject object : objects) {
            GameObjectRenderer.render(object, engine, display_root, cellSize * scale, showBoxCollisions);
        }

        // show actual TPS
        Label label = new Label(String.valueOf(engine.getActualTps()));
        label.setStyle("-fx-background-color: rgba(0, 0, 0,  0.6);-fx-text-fill: white;-fx-font-size: 1em; -fx-padding: 0.2em;");
        display_root.getChildren().add(label);
        translateSprite();

        //managing when a game is finished
        Team final_team = engine.isGameFinished();
        if( final_team != null && end_game != null){
            end_game.setContentText(" L'equipe " + final_team.name() + " a gagné ! " );
            if (!end_game.isShowing()){
                end_game.showAndWait();
            }
        }
    }


    public GridPane getGrid() {return grid;}
    public void setScale(double scale) {
        if (this.scale == scale) return;
        this.scale = scale;
        updateGridPane();
        translateSprite();
    }

    public boolean isShowBoxCollisions() {return showBoxCollisions;}
    public void switchShowBoxCollisions() {this.showBoxCollisions = !this.showBoxCollisions;}

    public double getScale() {
        return scale;
    }
}
