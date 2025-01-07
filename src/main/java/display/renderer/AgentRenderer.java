package display.renderer;

import engine.Team;
import engine.agent.Agent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class AgentRenderer {

    public static void render(Agent agent, Pane root, int tailleCase, boolean debug) {
        if(!agent.isInGame()) return;

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
        root.getChildren().add(agentView);

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

            root.getChildren().add(hitbox);
        }
    }
}
