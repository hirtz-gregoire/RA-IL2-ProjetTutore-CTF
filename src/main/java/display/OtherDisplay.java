package display;

import engine.Team;
import engine.agent.Agent;
import engine.map.Cell;
import engine.map.GameMap;
import engine.map.Ground;
import engine.map.Wall;
import engine.object.Flag;
import engine.object.Object;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;

import java.awt.*;
import java.util.List;

public class OtherDisplay extends Display {
    int tailleCase = 32;

    public OtherDisplay(Node node) {
        super(node);
    }

    @Override
    public void update(GameMap map, List<Agent> agents, List<Object> objects) {
        List<List<Cell>> cells = map.getCells();
        //Grille de la map
        GridPane grilleMap = new GridPane();
        for (int ligne = 0; ligne < cells.size(); ligne++) {
            for (int colonne = 0; colonne < cells.get(ligne).size(); colonne++) {
                Cell cell = cells.get(ligne).get(colonne);
                Image sprite = null;
                if (cell instanceof Ground) {
                    sprite = new Image("ressources/top/sol_neutre.png", tailleCase, tailleCase, false, false);
                }
                else if (cell instanceof Wall) {
                    sprite = new Image("ressources/top/mur_vue_haut.png", tailleCase, tailleCase, false, false);
                }
                ImageView imageView = new ImageView(sprite);
                GridPane.setConstraints(imageView, colonne, ligne);
                grilleMap.getChildren().add(imageView);
            }
        }
        //Stack Pane pour stocker la carte + Les objets dessus (agents)
        StackPane stackPane = new StackPane(grilleMap);
        for (Agent agent : agents) {
            int tailleAgent = agent.getRadius();
            String pathImageAgent = "ressources/top/robot/";
            if (agent.getTeam() == Team.BLUE) {
                pathImageAgent += "Rouge/robot_rose_flat_haut.png";
            }
            else if (agent.getTeam() == Team.PINK) {
                pathImageAgent += "Bleu/robot_bleu_flat_haut.png";
            }
            Image spriteAgent = new Image(pathImageAgent, tailleAgent, tailleAgent, false, false);
            ImageView agentView = new ImageView(spriteAgent);
            agentView.setX(agent.getCoordinate().getX());
            agentView.setY(agent.getCoordinate().getY());
            stackPane.getChildren().add(agentView);
        }
        for (Object object : objects) {
            String pathImageObjet = "ressources/top/";
            if (object instanceof Flag) {
                pathImageObjet += "drapeau_take_bleu_left.png";
            }
            Image spriteAgent = new Image(pathImageAgent, tailleCase, tailleCase, false, false);
            ImageView agentView = new ImageView(spriteAgent);
            agentView.setX(object.getCoordinate().getX());
            agentView.setY(object.getCoordinate().getY());
            stackPane.getChildren().add(agentView);
        }

        //Le display est uniquement le stackpane
        root = stackPane;
    }
}
