package display;

import display.renderer.AgentRenderer;
import display.renderer.GameObjectRenderer;
import engine.Engine;
import engine.Team;
import engine.agent.Agent;
import engine.map.Cell;
import engine.map.GameMap;
import engine.object.GameObject;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.util.List;

public class Display {

    private final Pane root;
    private final GridPane grid;
    private boolean showBoxCollisions = false;
    private int tailleCase;

    public Display(Pane pane, GameMap map, int taille) {
        this.root = pane;

        List<List<Cell>> cells = map.getCells();
        tailleCase = Math.round(taille / Math.max(cells.size(), cells.getFirst().size()*2));
        grid = new GridPane();
        for (int row = 0; row < cells.size(); row++) {
            for (int column = 0; column < cells.get(row).size(); column++) {
                Cell cell = cells.get(row).get(column);
                Image spriteCell = Team.getCellSprite(cell, tailleCase);
                ImageView imageView = new ImageView(spriteCell);
                GridPane.setConstraints(imageView, row, column);
                grid.getChildren().add(imageView);
            }
        }
        int maxHeight = cells.getFirst().size() * tailleCase;
        int maxWidth = cells.size() * tailleCase;
        root.setMaxSize(maxWidth, maxHeight);
    }

    public void update(Engine engine, GameMap map, List<Agent> agents, List<GameObject> objects) {
        root.getChildren().setAll(grid);
        // render agents
        for (Agent agent : agents) {
            AgentRenderer.render(agent, root, tailleCase, showBoxCollisions);
        }
        // render GameObjet
        for (GameObject object : objects) {
            GameObjectRenderer.render(object, engine, root, tailleCase, showBoxCollisions);
        }

        // show actual TPS
        Label label = new Label(String.valueOf(engine.getActualTps()));
        label.setStyle("-fx-background-color: white; -fx-padding: 0.2em;");
        root.getChildren().add(label);
    }


    public boolean isShowBoxCollisions() {return showBoxCollisions;}
    public void switchShowBoxCollisions() {this.showBoxCollisions = !this.showBoxCollisions;}
}
