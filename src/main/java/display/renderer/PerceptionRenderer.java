package display.renderer;

import engine.agent.Agent;
import ia.perception.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class PerceptionRenderer {
    public static void render(Perception perception, Pane root, Agent agent, int tailleCase) {
        switch (perception){
            case AgentCompass _, NearestAgentCompass _, NearestFlagCompass _, ObjectCompass _, TerritoryCompass _ -> {
                for(PerceptionValue perceptionValue : perception.getPerceptionValues()){
                    if(perceptionValue.vector().contains(Double.NaN)) continue;
                    double startX = agent.getCoordinate().x() * tailleCase;
                    double startY = agent.getCoordinate().y() * tailleCase;
                    double angle = Math.toRadians((perceptionValue.vector().getFirst()+agent.getAngular_position()));
                    double length = perceptionValue.vector().get(1);
                    double endX = (agent.getCoordinate().x() + Math.cos(angle) * length) * tailleCase;
                    double endY = (agent.getCoordinate().y() + Math.sin(angle) * length) * tailleCase;
                    Line compassLine = new Line(startX,startY, endX,endY);
                    compassLine.setStrokeWidth(3);
                    root.getChildren().add(compassLine);
                    System.out.println(perceptionValue.type()+" - "+angle+" "+Math.cos(angle)+" "+Math.sin(angle));
                    System.out.println(endX + " " + endY);
                }
            }
            case PerceptionRaycast _ -> {
                Line rayLine;
                PerceptionRaycast raycast = (PerceptionRaycast) perception;

                double startX = agent.getCoordinate().x() * tailleCase;
                double startY = agent.getCoordinate().y() * tailleCase;
                Double length = raycast.getRaySize();

                double offset = (raycast.getRayCount() < 3) ? raycast.getViewAngle() / (raycast.getRayCount() + 1) : raycast.getViewAngle() / (raycast.getRayCount() - 1);
                int i = (raycast.getRayCount() < 3) ? 1 : 0;
                int drawnRays = 0;

                while (drawnRays < raycast.getRayCount()) {
                    double angle = Math.toRadians(i * offset - raycast.getViewAngle()/2) + Math.toRadians(agent.getAngular_position());
                    double endX = (agent.getCoordinate().x() + Math.cos(angle) * length) * tailleCase;
                    double endY = (agent.getCoordinate().y() + Math.sin(angle) * length) * tailleCase;

                    i++;
                    drawnRays++;

                    rayLine = new Line(startX,startY, endX,endY);
                    rayLine.setStrokeWidth(3);
                    rayLine.setStroke(Color.BLACK);
                    root.getChildren().add(rayLine);
                }


                for(PerceptionValue perceptionValue : raycast.getPerceptionValues()){
                    if(perceptionValue.vector().contains(Double.NaN)) continue;

                    length = perceptionValue.vector().get(1);
                    double angle = Math.toRadians((perceptionValue.vector().getFirst()));
                    double endX = (agent.getCoordinate().x() + Math.cos(angle) * length) * tailleCase;
                    double endY = (agent.getCoordinate().y() + Math.sin(angle) * length) * tailleCase;

                    rayLine = new Line(startX,startY, endX,endY);
                    rayLine.setStrokeWidth(3);
                    rayLine.setStroke(Color.RED);
                    root.getChildren().add(rayLine);
                    //System.out.println(perceptionValue.type()+" - "+angle+" "+Math.cos(angle)+" "+Math.sin(angle));
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + perception.getClass());
        }
    }
}
