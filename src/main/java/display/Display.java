package display;

import display.renderer.AgentRenderer;
import display.renderer.GameObjectRenderer;
import engine.Engine;
import engine.Team;
import engine.agent.Agent;
import engine.map.Cell;
import engine.map.GameMap;
import engine.object.Flag;
import engine.object.GameObject;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.List;

public class Display {

    private Pane root;
    private final GridPane grid;
    private boolean debug = false;
    private int tailleCase;

    public Display(Pane pane, GameMap map, int taille, Label labelTpsActualEngine, Label[] labelsNbJoueursMorts, Label[] labelsTempsProchaineReaparition) {
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

        this.root.getChildren().add(grid);
    }

    public void update(Engine engine, GameMap map, List<Agent> agents, List<GameObject> objects) {
        root.getChildren().clear();
        this.root.getChildren().add(grid);


        // render agents
        for (Agent agent : agents) {
            AgentRenderer.render(agent, root, tailleCase, debug);
        }
        // render GameObjet
        for (GameObject object : objects) {
            GameObjectRenderer.render(object, engine, root, tailleCase, debug);
        }

    }
}
