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
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Display {

    private final Pane root;
    private final GridPane grid;
    private boolean showBoxCollisions = false;
    private Map<PerceptionType,Boolean> desiredPerceptions = new HashMap<>();
    private int cellSize;

    public Display(Pane pane, GameMap map, int taille, Map<PerceptionType, Boolean> desiredPerceptions) {
        this.root = pane;
        this.desiredPerceptions = desiredPerceptions;

        List<List<Cell>> cells = map.getCells();
        cellSize = Math.round(taille / Math.max(cells.size(), cells.getFirst().size() / 2));
        grid = new GridPane();
        for (int row = 0; row < cells.size(); row++) {
            for (int column = 0; column < cells.get(row).size(); column++) {
                Cell cell = cells.get(row).get(column);
                Image spriteCell = Team.getCellSprite(cell, cellSize);
                ImageView imageView = new ImageView(spriteCell);
                GridPane.setConstraints(imageView, row, column);
                grid.getChildren().add(imageView);
            }
        }
        int maxHeight = cells.getFirst().size() * cellSize;
        int maxWidth = cells.size() * cellSize;
        root.setMaxSize(maxWidth, maxHeight);
        root.setClip(new Rectangle(maxWidth, maxHeight));
    }

    public void update(Engine engine, GameMap map, List<Agent> agents, List<GameObject> objects) {
        root.getChildren().setAll(grid);
        // render agents
        for (Agent agent : agents) {
            AgentRenderer.render(agent, root, cellSize, showBoxCollisions, desiredPerceptions);
        }
        // render GameObjet
        for (GameObject object : objects) {
            GameObjectRenderer.render(object, engine, root, cellSize, showBoxCollisions);
        }

        // show actual TPS
        Label label = new Label(String.valueOf(engine.getActualTps()));
        label.setStyle("-fx-background-color: white; -fx-padding: 0.2em;");
        root.getChildren().add(label);
    }


    public boolean isShowBoxCollisions() {return showBoxCollisions;}
    public void switchShowBoxCollisions() {this.showBoxCollisions = !this.showBoxCollisions;}
}
