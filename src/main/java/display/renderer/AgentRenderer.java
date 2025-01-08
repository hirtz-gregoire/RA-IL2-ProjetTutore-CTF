package display.renderer;

import engine.Team;
import engine.agent.Agent;
import ia.perception.Perception;
import ia.perception.PerceptionType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

import java.util.Map;

public class AgentRenderer {

    /**
     * Static method to render the sprite of an agent
     * @param agent Agent to render
     * @param root Pane to which add the sprite
     * @param cellSize Size of one cell of the grid
     * @param showBoxCollisions If debug should be displayed or not
     */
    public static void render(Agent agent, Pane root, int cellSize, boolean showBoxCollisions, Map<PerceptionType, Boolean> desiredPerceptions) {
        if(!agent.isInGame()) return;

        //Le sprite de l'agent est un carré qui a pour longueur le diamètre de la hitbox de l'agent
        int tailleAgent = (int) (agent.getRadius() * 2 * cellSize);
        Image spriteAgent = Team.getAgentSprite(agent, tailleAgent);
        ImageView agentView = new ImageView(spriteAgent);

        //Rotationner le sprite de l'agent, son angular position commence à 0 en bas et tourne dans le sens inverse des aiguilles d'une montre, la méthode setRotate démarre d'en haut et fonctionne dans le sens des aiguilles d'un montre
        agentView.setRotate(agent.getAngular_position()-90);

        double newPosX = agent.getCoordinate().x()*cellSize - (double) tailleAgent /2;
        double newPosY = agent.getCoordinate().y()*cellSize - (double) tailleAgent /2;
        agentView.setX(newPosX);
        agentView.setY(newPosY);
        root.getChildren().add(agentView);

        if(showBoxCollisions){
            Circle hitbox = new Circle();
            hitbox.setRadius(agent.getRadius() * cellSize);

            hitbox.setCenterX(agent.getCoordinate().x()*cellSize - agent.getRadius() /2);
            hitbox.setCenterY(agent.getCoordinate().y()*cellSize - agent.getRadius() /2);

            hitbox.setFill(Team.TeamToColor(agent.getTeam()));
            hitbox.setOpacity(0.6);

            root.getChildren().add(hitbox);
        }

        if (!desiredPerceptions.isEmpty()){
            for (Perception perception : agent.getModel().getPerceptions()){
                PerceptionRenderer.render(perception, root, cellSize, desiredPerceptions);
            }
        }
    }
}
