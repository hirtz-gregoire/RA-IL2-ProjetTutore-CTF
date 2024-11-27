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
import javafx.scene.shape.Rectangle;

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
                Image sprite = Team.getCellSprite(cell, tailleCase);
                ImageView imageView = new ImageView(sprite);
                GridPane.setConstraints(imageView, colonne, ligne);
                grilleMap.getChildren().add(imageView);
            }
        }
        //Stack Pane pour stocker la carte + Les objets dessus (agents)
        StackPane stackPane = new StackPane(grilleMap);
        for (Agent agent : agents) {
            int tailleAgent = (int) tailleCase/2;
            Image spriteAgent = Team.getAgentSprite(agent, tailleAgent);
            ImageView agentView = new ImageView(spriteAgent);
            int nombreMagique = 20;

            int newPosX = (int) agent.getCoordinate().x()*tailleCase - (map.getCells().size() * tailleCase) /2;
            int newPosY = (int) agent.getCoordinate().y()*tailleCase - (map.getCells().getFirst().size() * tailleCase) /2;
            agentView.setTranslateX(newPosX);
            agentView.setTranslateY(newPosY);
            stackPane.getChildren().add(agentView);
        }
        stackPane.getChildren().add(new Rectangle(400, 150, 8, 8));
        for (GameObject object : objects) {
            String pathImageObjet = "file:ressources/top/";
            if (object instanceof Flag) {
                if (((Flag) object).getTeam() == Team.RED) {
                    pathImageObjet += "drapeau_rouge.png";
                }
                else {
                    pathImageObjet += "drapeau_bleu.png";
                }
            }
            Image spriteAgent = new Image(pathImageObjet, tailleCase, tailleCase, false, false);
            ImageView agentView = new ImageView(spriteAgent);
            agentView.setTranslateX(object.getCoordinate().x()*tailleCase - map.getCells().size()*tailleCase);
            agentView.setTranslateY(object.getCoordinate().y()*tailleCase - map.getCells().getFirst().size()*tailleCase);
            stackPane.getChildren().add(agentView);
        }

        //Le display est uniquement le stackpane
        root.getChildren().clear();
        root.getChildren().add(stackPane);
    }
}
