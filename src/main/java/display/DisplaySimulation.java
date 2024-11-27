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
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import views.Observateur;
import views.VueSimulationMain;

import java.util.List;

//Display qui affiche la carte de jeu avec les boutons
public class DisplaySimulation extends Display {
    int tailleCase = 32;

    public DisplaySimulation(Node simulationBox) {
        super(simulationBox);
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
            int tailleAgent = (int) agent.getRadius();
            String pathImageAgent = "ressources/top/robot/";
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
            String pathImageObjet = "ressources/top/";
            if (object instanceof Flag) {
                pathImageObjet += "drapeau_take_bleu_left.png";
            }
            Image spriteAgent = new Image(pathImageObjet, tailleCase, tailleCase, false, false);
            ImageView agentView = new ImageView(spriteAgent);
            agentView.setX(object.getCoordinate().x());
            agentView.setY(object.getCoordinate().y());
            stackPane.getChildren().add(agentView);
        }

        //Le display est uniquement la vbox
        root = stackPane;
    }
}
