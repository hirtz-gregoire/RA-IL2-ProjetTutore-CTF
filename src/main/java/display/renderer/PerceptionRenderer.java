package display.renderer;

import engine.agent.Agent;
import ia.perception.*;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;

public class PerceptionRenderer {
    public static void render(Perception perception, Pane root, Agent agent, int tailleCase) {
        switch (perception){
            case AgentCompass _, NearestAgentCompass _, NearestFlagCompass _, ObjectCompass _, TerritoryCompass _ ->{
                for(PerceptionValue perceptionValue : perception.getPerceptionValues()){
                    if(perceptionValue.vector().contains(Double.NaN)) continue;
                    double startX = agent.getCoordinate().x() * tailleCase;
                    double startY = agent.getCoordinate().y() * tailleCase;
                    double angle = Math.toRadians((perceptionValue.vector().getFirst()+agent.getAngular_position()));
                    double endX = (agent.getCoordinate().x() + Math.cos(angle)) * tailleCase;
                    double endY = (agent.getCoordinate().y() + Math.sin(angle)) * tailleCase;
                    Line compassLine = new Line(startX,startY, endX,endY);
                    compassLine.setStrokeWidth(3);
                    root.getChildren().add(compassLine);
                    System.out.println(perceptionValue.type()+" - "+angle+" "+Math.cos(angle)+" "+Math.sin(angle));
                }
            }
            case PerceptionRaycast _ -> System.out.println("PerceptionRaycast");
            default -> throw new IllegalStateException("Unexpected value: " + perception.getClass());
        }
    }
}
