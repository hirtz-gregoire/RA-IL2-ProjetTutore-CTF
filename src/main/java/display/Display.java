package display;

import engine.Engine;
import engine.Team;
import engine.agent.Agent;
import engine.map.*;
import engine.object.Flag;
import engine.object.GameObject;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.List;

//Display qui affiche la carte de jeu
public class Display {
    //taille en pixel
    int tailleCase;
    //La gridpane (noeux javafx) contenant l'affichage de la carte (toutes les cases) initilisé dans le constructeur
    private GridPane gridPaneCarte;
    //Le pane dans lequel le display affiche le jeu
    public Pane root = null;
    private Label labelTps;
    private Label[] labelsJoueursMortsEquipes;
    private Label[] labelsTempsReaparitionEquipes;
    private boolean debug;

    public Display(Pane simulationBox, GameMap map, String taille, Label labelTpsActualEngine, Label[] labelsNbJoueursMorts, Label[] labelsTempsProchaineReaparition) {
        root = simulationBox;
        List<List<Cell>> cells = map.getCells();
        //On a 2 types de taille des cases "petit" pour affichage dans le sélécteur de carte et "grand" pour l'affichage dans la simulation
        if (taille.equals("grand")) {
            //Adpapter taille des cases en fonction de la taille de la carte (random value parce que voilà)
            tailleCase = Math.round(1024 / Math.max(cells.size(), cells.getFirst().size() / 2));
        }
        else {
            tailleCase = Math.round(128 / Math.max(cells.size(), cells.getFirst().size() / 2));
        }
        //Grille de la map
        GridPane gridPaneCarte = new GridPane();
        for (int row = 0; row < cells.size(); row++) {
            for (int column = 0; column < cells.get(row).size(); column++) {
                Cell cell = cells.get(row).get(column);
                Image spriteCell = Team.getCellSprite(cell, tailleCase);
                ImageView imageView = new ImageView(spriteCell);
                GridPane.setConstraints(imageView, row, column);
                gridPaneCarte.getChildren().add(imageView);
            }
        }
        this.gridPaneCarte = gridPaneCarte;
        labelTps = labelTpsActualEngine;
        labelsJoueursMortsEquipes = labelsNbJoueursMorts;
        labelsTempsReaparitionEquipes = labelsTempsProchaineReaparition;
    }

    public void update(Engine engine, GameMap map, List<Agent> agents, List<GameObject> objects) {
        root.getChildren().clear();

        //Stack Pane pour stocker la carte + Les objets dessus (agents)
        Pane pane = new Pane(this.gridPaneCarte);

        labelTps.setText("TPS actuels : " + engine.getActualTps());
        for (int numEquipe = 0; numEquipe < engine.getNbEquipes(); numEquipe++) {
            labelsJoueursMortsEquipes[numEquipe].setText("Nombre joueur mort équipe " + Team.numEquipeToString(numEquipe+1) + " : " + engine.getNbJoueursMortsByNumEquipe(numEquipe+1));
            labelsTempsReaparitionEquipes[numEquipe].setText("Temps prochaine réaparition équipe " + Team.numEquipeToString(numEquipe+1) + " : " + engine.getTempsReaparitionByNumEquipe(numEquipe+1));
        }

        pane.setMaxHeight(this.gridPaneCarte.getHeight());
        pane.setMaxWidth(this.gridPaneCarte.getWidth());

        for (Agent agent : agents) {
            if(!agent.isInGame()) continue;

            //Le sprite de l'agent est un carré qui a pour longueur le diamètre de la hitbox de l'agent
            int tailleAgent = (int) (agent.getRadius() * 2 * tailleCase);
            Image spriteAgent = Team.getAgentSprite(agent, tailleAgent);
            ImageView agentView = new ImageView(spriteAgent);
            //Rotationner le sprite de l'agent, son angular position commence à 0 en bas et tourne dans le sens inverse des aiguilles d'une montre, la méthode setRotate démarre d'en haut et fonctionne dans le sens des aiguilles d'un montre
            agentView.setRotate(agent.getAngular_position()-90);

            double newPosX = agent.getCoordinate().x()*tailleCase - (double) tailleAgent /2;
            double newPosY = agent.getCoordinate().y()*tailleCase - (double) tailleAgent /2;
            agentView.setX(newPosX);
            agentView.setY(newPosY);
            pane.getChildren().add(agentView);

            if(debug){
                Circle hitbox = new Circle();
                hitbox.setRadius(agent.getRadius() * tailleCase);

                hitbox.setCenterX(agent.getCoordinate().x()*tailleCase - agent.getRadius() /2);
                hitbox.setCenterY(agent.getCoordinate().y()*tailleCase - agent.getRadius() /2);

                switch (agent.getTeam()) {
                    case RED -> hitbox.setFill(Color.RED);
                    case null, default -> hitbox.setFill(Color.BLUE);
                }
                hitbox.setOpacity(0.6);

                pane.getChildren().add(hitbox);
            }
        }


        for (GameObject object : objects) {
            int tailleObject = tailleCase;
            Image spriteObject = Team.getObjectSprite((Flag)object, tailleObject);
            ImageView objetView = new ImageView(spriteObject);
            objetView.setTranslateX(object.getCoordinate().x()*tailleCase - (double) tailleObject/2);
            objetView.setTranslateY(object.getCoordinate().y()*tailleCase - (double) tailleObject/2);
            pane.getChildren().add(objetView);

            if(object instanceof Flag && !((Flag) object).getHolded()){
                Circle safeZone = new Circle();

                double safeZoneRadius = engine.getFlagSafeZoneRadius();
                safeZone.setRadius(safeZoneRadius * tailleCase);

                safeZone.setCenterX(object.getCoordinate().x()*tailleCase - safeZoneRadius /2);
                safeZone.setCenterY(object.getCoordinate().y()*tailleCase - safeZoneRadius /2);

                safeZone.setStroke(Color.WHITE);
                safeZone.setFill(Color.TRANSPARENT);
                safeZone.setStrokeWidth(1);

                pane.getChildren().add(safeZone);
                if(debug){
                    Circle hitbox = new Circle();

                    double hitboxRadius = engine.FLAG_RADIUS;
                    hitbox.setRadius(hitboxRadius * tailleCase);

                    hitbox.setCenterX(object.getCoordinate().x()*tailleCase - hitboxRadius /2);
                    hitbox.setCenterY(object.getCoordinate().y()*tailleCase - hitboxRadius /2);

                    hitbox.setFill(Color.WHITE);
                    hitbox.setOpacity(0.6);
                    pane.getChildren().add(hitbox);
                }
            }
            Image spriteAgent = Team.getObjectSprite(object, tailleObject);
            ImageView agentView = new ImageView(spriteAgent);
            agentView.setTranslateX(object.getCoordinate().x()*tailleCase - (double) tailleObject/2);
            agentView.setTranslateY(object.getCoordinate().y()*tailleCase - (double) tailleObject/2);
            pane.getChildren().add(agentView);

            if(object instanceof Flag && !((Flag) object).getHolded()){
                Circle safeZone = new Circle();

                double safeZoneRadius = engine.getFlagSafeZoneRadius();
                safeZone.setRadius(safeZoneRadius * tailleCase);

                safeZone.setCenterX(object.getCoordinate().x()*tailleCase - safeZoneRadius /2);
                safeZone.setCenterY(object.getCoordinate().y()*tailleCase - safeZoneRadius /2);

                safeZone.setStroke(Color.WHITE);
                safeZone.setFill(Color.TRANSPARENT);
                safeZone.setStrokeWidth(1);

                pane.getChildren().add(safeZone);
                if(debug){
                    Circle hitbox = new Circle();

                    double hitboxRadius = engine.FLAG_RADIUS;
                    hitbox.setRadius(hitboxRadius * tailleCase);

                    hitbox.setCenterX(object.getCoordinate().x()*tailleCase - hitboxRadius /2);
                    hitbox.setCenterY(object.getCoordinate().y()*tailleCase - hitboxRadius /2);

                    hitbox.setFill(Color.WHITE);
                    hitbox.setOpacity(0.6);
                    pane.getChildren().add(hitbox);
                }
            }
        }

        //Le display est uniquement le stackpane
        root.getChildren().add(pane);
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
    public boolean getDebug() {
        return debug;
    }
    public GridPane getGridPaneCarte() {
        return gridPaneCarte;
    }
}
