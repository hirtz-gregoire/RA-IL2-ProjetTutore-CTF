package display;

import engine.Team;
import engine.agent.Agent;
import engine.map.*;
import engine.object.Flag;
import engine.object.GameObject;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.image.Image;

import java.util.List;

//Display qui affiche la carte de jeu
public class Display {
    //taille en pixel
    int tailleCase;
    //La gridpane (noeux javafx) contenant l'affichage de la carte (toutes les cases) initilisé dans le constructeur
    private GridPane gridPaneCarte;
    //Le pane dans lequel le display affiche le jeu
    public Pane root = null;

    public Display(Pane simulationBox, GameMap map, String taille) {
        root = simulationBox;
        List<List<Cell>> cells = map.getCells();
        //On a 2 types de taille des cases "petit" pour affichage dans le sélécteur de carte et "grand" pour l'affichage dans la simulation
        if (taille.equals("grand")) {
            //Adpapter taille des cases en fonction de la taille de la carte (random value parce que voilà)
            tailleCase = (int) (512 / Math.round(Math.max(cells.size(), cells.getFirst().size() / 2)));
        }
        else {
            tailleCase = (int) (256 / Math.round(Math.max(cells.size(), cells.getFirst().size() / 2)));
        }
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
    public GridPane getGridPaneCarte() {
        return this.gridPaneCarte;
    }
}
