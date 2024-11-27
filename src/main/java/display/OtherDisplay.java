package display;

import engine.Coordinate;
import engine.Team;
import engine.agent.Agent;
import engine.map.Cell;
import engine.map.GameMap;
import engine.map.Ground;
import engine.map.Wall;
import engine.object.Flag;
import engine.object.GameObject;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.paint.Paint;

import java.awt.*;
import java.util.List;

public class OtherDisplay extends Display {
    int tailleCase = 32;

    public OtherDisplay(Pane node) {
        super(node);
    }

    @Override
    public void update(GameMap map, List<Agent> agents, List<GameObject> objects) {
        List<List<Cell>> cells = map.getCells();
        //Grille de la map
        GridPane grilleMap = new GridPane();
        for (int ligne = 0; ligne < cells.size(); ligne++) {
            for (int colonne = 0; colonne < cells.get(ligne).size(); colonne++) {
                Cell cell = cells.get(ligne).get(colonne);
                Image sprite = null;
                if (cell instanceof Ground) {
                    sprite = new Image("file:ressources/top/sol_neutre.png", tailleCase, tailleCase, false, false);
                }
                else if (cell instanceof Wall) {
                    sprite = new Image("file:ressources/top/mur_vue_haut.png", tailleCase, tailleCase, false, false);
                }
                ImageView imageView = new ImageView(sprite);
                GridPane.setConstraints(imageView, colonne, ligne);
                grilleMap.getChildren().add(imageView);
            }
        }
        //Stack Pane pour stocker la carte + Les objets dessus (agents)
        StackPane stackPane = new StackPane(grilleMap);
        for (Agent agent : agents) {
            double tailleAgent = agent.getRadius();
            String pathImageAgent = "file:ressources/top/robot/";
            if (agent.getTeam() == Team.BLUE) {
                pathImageAgent += "Rouge/robot_rose_flat_haut.png";
            }
            else if (agent.getTeam() == Team.PINK) {
                pathImageAgent += "Bleu/robot_bleu_flat_haut.png";
            }
            Image spriteAgent = new Image(pathImageAgent, tailleAgent, tailleAgent, false, false);
            ImageView agentView = new ImageView(spriteAgent);
            agentView.setX(agent.getCoordinate().x());
            agentView.setY(agent.getCoordinate().y());
            stackPane.getChildren().add(agentView);
        }
        for (GameObject object : objects) {
            String pathImageObjet = "file:ressources/top/";
            if (object instanceof Flag) {
                pathImageObjet += "drapeau_take_bleu_left.png";
            }
            Image spriteAgent = new Image(pathImageObjet, tailleCase, tailleCase, false, false);
            ImageView agentView = new ImageView(spriteAgent);
            agentView.setX(object.getCoordinate().x());
            agentView.setY(object.getCoordinate().y());
            stackPane.getChildren().add(agentView);
        }

        //System.out.println("bonjour");

        //Le display est uniquement le stackpane
        root.getChildren().clear();
        root.getChildren().add(stackPane);
    }
}
