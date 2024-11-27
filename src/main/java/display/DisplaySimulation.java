package display;

import engine.Team;
import engine.agent.Agent;
import engine.map.*;
import engine.object.Flag;
import engine.object.GameObject;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;

import java.util.List;

//Display qui affiche la carte de jeu avec les boutons
public class DisplaySimulation extends Display {
    int tailleCase = 64;

    public DisplaySimulation(Pane simulationBox) {
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
                String pathImageCell = "file:ressources/top/";
                //Détection du type de case
                if (cell instanceof Ground || cell instanceof SpawningCell) {
                    //Détection de l'équipe
                    if (cell.getTeam() == Team.BLUE) {
                        pathImageCell +="sol_vert.png";
                    }
                    else if (cell.getTeam() == Team.PINK) {
                        pathImageCell +="sol_rouge.png";
                    }
                    else {
                        pathImageCell +="sol_neutre.png";
                   }
                }
                else if (cell instanceof Wall) {
                    pathImageCell += "mur_vue_haut.png";
                }
                Image sprite = new Image(pathImageCell, tailleCase, tailleCase, false, false);
                ImageView imageView = new ImageView(sprite);
                GridPane.setConstraints(imageView, colonne, ligne);
                grilleMap.getChildren().add(imageView);
            }
        }
        //Stack Pane pour stocker la carte + Les objets dessus (agents)
        StackPane stackPane = new StackPane(grilleMap);
        for (Agent agent : agents) {
            double tailleAgent = 32;
            String pathImageAgent = "file:ressources/top/robot/";
            if (agent.getTeam() == Team.BLUE) {
                pathImageAgent += "Rouge/robot_rose_flat_haut.png";
            }
            else if (agent.getTeam() == Team.PINK) {
                pathImageAgent += "Bleu/robot_bleu_flat_haut.png";
            }
            Image spriteAgent = new Image(pathImageAgent, tailleAgent, tailleAgent, false, false);
            ImageView agentView = new ImageView(spriteAgent);
            agentView.setTranslateX(agent.getCoordinate().x());
            agentView.setTranslateY(agent.getCoordinate().y());
            stackPane.getChildren().add(agentView);
        }
        for (GameObject object : objects) {
            String pathImageObjet = "file:ressources/top/";
            if (object instanceof Flag) {
                if (((Flag) object).getTeam() == Team.PINK) {
                    pathImageObjet += "drapeau_rouge.png";
                }
                else {
                    pathImageObjet += "drapeau_bleu.png";
                }
            }
            Image spriteAgent = new Image(pathImageObjet, tailleCase, tailleCase, false, false);
            ImageView agentView = new ImageView(spriteAgent);
            agentView.setTranslateX(object.getCoordinate().x());
            agentView.setTranslateY(object.getCoordinate().y());
            stackPane.getChildren().add(agentView);
        }

        //Le display est uniquement le stackpane
        root.getChildren().clear();
        root.getChildren().add(stackPane);
    }
}
