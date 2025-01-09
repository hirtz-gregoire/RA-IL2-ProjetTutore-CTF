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
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class Display {

    private final Pane root;
    private final GridPane grid;
    private boolean showBoxCollisions = false;
    private Map<PerceptionType,Boolean> desiredPerceptions = new HashMap<>();
    private double cellSize;
    private List<List<Cell>> cells;

    private double scale = 1;
    private double currentPosX = 0;
    private double currentPosY = 0;

    public Display(Pane pane, GameMap map, int taille, Map<PerceptionType, Boolean> desiredPerceptions) {
        this.root = pane;
        this.desiredPerceptions = desiredPerceptions;

        cells = map.getCells();
        cellSize = Math.round(taille / Math.max(cells.size(), cells.getFirst().size() / 2));
        grid = new GridPane();

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
        System.out.println(maxHeight+" "+maxWidth);
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
        root.getChildren().setAll(grid);
        // render agents
        for (Agent agent : agents) {
            AgentRenderer.render(agent, root, cellSize * scale, showBoxCollisions, desiredPerceptions);
        }
        // render GameObjet
        for (GameObject object : objects) {
            GameObjectRenderer.render(object, engine, root, cellSize * scale, showBoxCollisions);
        }

        // show actual TPS
        Label label = new Label(String.valueOf(engine.getActualTps()));
        label.setStyle("-fx-background-color: white; -fx-padding: 0.2em;");
        root.getChildren().add(label);
        translateSprite();

        if(false){
            currentPosX = - agents.get(0).getCoordinate().x() * cellSize * (scale-1);
            currentPosY = - agents.get(0).getCoordinate().y() * cellSize * (scale-1);
            translateSprite();
            System.out.println(currentPosX+" "+currentPosY+" "+agents.getFirst().getTeam());
        }
    }


    public boolean isShowBoxCollisions() {return showBoxCollisions;}
    public void switchShowBoxCollisions() {this.showBoxCollisions = !this.showBoxCollisions;}

    public GridPane getGrid() {return grid;}
    public void setScale(double scale) {
        this.scale = scale;
        updateGridPane();
        translateSprite();
    }

    public double getScale() {
        return scale;
    }
}
