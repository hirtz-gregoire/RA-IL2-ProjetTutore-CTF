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
import javafx.scene.shape.Line;
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
        Pane pane = new Pane(grilleMap);

        //Le display est uniquement le stackpane
        root.getChildren().clear();
        root.getChildren().add(pane);


        for (Agent agent : agents) {
            if(!agent.isInGame()) continue;
            int tailleAgent = tailleCase /2;
            Image spriteAgent = Team.getAgentSprite(agent, tailleAgent);
            ImageView agentView = new ImageView(spriteAgent);
            //Rotationner le sprite de l'agent
            agentView.setRotate(agent.getAngular_position()+0);
            //Vecteur de déplacmenet de l'agent
            Line vecteurAgent = new Line();
            vecteurAgent.setStartY(agent.getCoordinate().x());
            vecteurAgent.setStartX(agent.getCoordinate().y());
            vecteurAgent.setEndY(agent.getCoordinate().x() + agent.getSpeed() * tailleCase * 2 * Math.cos(Math.toRadians(agent.getAngular_position())));
            vecteurAgent.setEndX(agent.getCoordinate().y() + agent.getSpeed()* tailleCase * 2 * Math.sin(Math.toRadians(agent.getAngular_position())));
            vecteurAgent.setTranslateY(agent.getCoordinate().x() * tailleCase);
            vecteurAgent.setTranslateX(agent.getCoordinate().y() * tailleCase);

            // System.out.println(stackPane.getWidth());
            // System.out.println(stackPane.getHeight());
            // System.out.println("root : "+root.getWidth());
            // System.out.println("root : "+root.getHeight());

            double newPosX = agent.getCoordinate().y()*tailleCase - (double) tailleAgent /2;
            double newPosY = agent.getCoordinate().x()*tailleCase - (double) tailleAgent /2;
            agentView.setX(newPosX);
            agentView.setY(newPosY);
            pane.getChildren().add(agentView);
            pane.getChildren().add(vecteurAgent);
        }


        for (GameObject object : objects) {
            String pathImageObjet = "file:ressources/top/";
            if (object instanceof Flag) {
                //System.out.println("Display flag : "+object.getCoordinate()+" - "+((Flag) object).getTeam()+" : "+((Flag) object).getHolded());
                if (((Flag) object).getTeam() == Team.RED) {
                    pathImageObjet += "drapeau_rouge.png";
                }
                else {
                    pathImageObjet += "drapeau_bleu.png";
                }
            }
            Image spriteAgent = new Image(pathImageObjet, tailleCase, tailleCase, false, false);
            ImageView agentView = new ImageView(spriteAgent);
            agentView.setTranslateX(object.getCoordinate().y()*tailleCase - (double) tailleCase/2);
            agentView.setTranslateY(object.getCoordinate().x()*tailleCase - (double) tailleCase/2);
            pane.getChildren().add(agentView);
        }
    }
}
