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
    //La gridpane (noeux javafx) contenant l'affichage de la carte (toutes les cases) initilisé dans le constructeur
    GridPane gridPaneCarte;

    public DisplaySimulation(Pane simulationBox, GameMap map) {
        super(simulationBox);
        List<List<Cell>> cells = map.getCells();
        //Grille de la map
        GridPane gridPane = new GridPane();
        for (int ligne = 0; ligne < cells.size(); ligne++) {
            for (int colonne = 0; colonne < cells.get(ligne).size(); colonne++) {
                Cell cell = cells.get(ligne).get(colonne);
                Image sprite = Team.getCellSprite(cell, tailleCase);
                ImageView imageView = new ImageView(sprite);
                GridPane.setConstraints(imageView, colonne, ligne);
                gridPane.getChildren().add(imageView);
            }
        }
        this.gridPaneCarte = gridPane;
    }

    @Override
    public void update(GameMap map, List<Agent> agents, List<GameObject> objects) {
        root.getChildren().clear();

        //Stack Pane pour stocker la carte + Les objets dessus (agents)
        Pane pane = new Pane(this.gridPaneCarte);

        for (Agent agent : agents) {
            if(!agent.isInGame()) continue;
            //Le sprite de l'agent est un carré qui a pour longueur le diamètre de la hitbox de l'agent
            int tailleAgent = (int) (agent.getRadius() * 2 * tailleCase);
            Image spriteAgent = Team.getAgentSprite(agent, tailleAgent);
            ImageView agentView = new ImageView(spriteAgent);
            //Rotationner le sprite de l'agent, son angular position commence à 0 en bas et tourne dans le sens inverse des aiguilles d'une montre, la méthode setRotate démarre d'en haut et fonctionne dans le sens des aiguilles d'un montre
            agentView.setRotate(-agent.getAngular_position());

            double newPosX = agent.getCoordinate().y()*tailleCase - (double) tailleAgent /2;
            double newPosY = agent.getCoordinate().x()*tailleCase - (double) tailleAgent /2;
            agentView.setX(newPosX);
            agentView.setY(newPosY);
            pane.getChildren().add(agentView);
        }


        for (GameObject object : objects) {
            int tailleObject = tailleCase;
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
            Image spriteAgent = new Image(pathImageObjet, tailleObject, tailleObject, false, false);
            ImageView agentView = new ImageView(spriteAgent);
            agentView.setTranslateX(object.getCoordinate().y()*tailleCase - (double) tailleObject/2);
            agentView.setTranslateY(object.getCoordinate().x()*tailleCase - (double) tailleObject/2);
            pane.getChildren().add(agentView);
        }

        //Le display est uniquement le stackpane
        root.getChildren().add(pane);
    }
}
