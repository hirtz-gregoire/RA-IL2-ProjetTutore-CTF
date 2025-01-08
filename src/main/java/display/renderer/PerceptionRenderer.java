package display.renderer;

import engine.agent.Agent;
import ia.perception.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class PerceptionRenderer {
    /**
     * Static method to display a perception
     * @param perception The perception to display
     * @param root The pane on which display
     * @param cellSize Size of a cell of the grid
     */
    public static void render(Perception perception, Pane root, int cellSize) {
        Agent agent = perception.getMy_agent();

        switch (perception) {
            case AgentCompass _, NearestAgentCompass _, NearestFlagCompass _, ObjectCompass _, TerritoryCompass _ -> {
                for(PerceptionValue perceptionValue : perception.getPerceptionValues()){
                    if(perceptionValue.vector().contains(Double.NaN)) continue;
                    double startX = agent.getCoordinate().x() * cellSize;
                    double startY = agent.getCoordinate().y() * cellSize;
                    double angle = Math.toRadians((perceptionValue.vector().getFirst()+agent.getAngular_position()));
                    double length = perceptionValue.vector().get(1);
                    double endX = (agent.getCoordinate().x() + Math.cos(angle) * length) * cellSize;
                    double endY = (agent.getCoordinate().y() + Math.sin(angle) * length) * cellSize;
                    Line compassLine = new Line(startX,startY, endX,endY);
                    compassLine.setStrokeWidth(3);
                    root.getChildren().add(compassLine);
                }
            }
            case PerceptionRaycast _ -> {
                Line rayLine;
                PerceptionRaycast raycast = (PerceptionRaycast) perception;

                double startX = agent.getCoordinate().x() * cellSize;
                double startY = agent.getCoordinate().y() * cellSize;
                Double length = raycast.getRaySize();

                double offset = (raycast.getRayCount() < 3) ? raycast.getViewAngle() / (raycast.getRayCount() + 1) : raycast.getViewAngle() / (raycast.getRayCount() - 1);
                int i = (raycast.getRayCount() < 3) ? 1 : 0;
                int drawnRays = 0;

                while (drawnRays < raycast.getRayCount()) {
                    double angle = Math.toRadians(i * offset - raycast.getViewAngle()/2) + Math.toRadians(agent.getAngular_position());
                    double endX = (agent.getCoordinate().x() + Math.cos(angle) * length) * cellSize;
                    double endY = (agent.getCoordinate().y() + Math.sin(angle) * length) * cellSize;

                    i++;
                    drawnRays++;

                    rayLine = new Line(startX,startY, endX,endY);
                    rayLine.setStrokeWidth(3);
                    rayLine.setStroke(Color.BLACK);
                    rayLine.setOpacity(0.4);
                    root.getChildren().add(rayLine);
                }


                for(PerceptionValue perceptionValue : raycast.getPerceptionValues()){
                    if(perceptionValue.vector().contains(Double.NaN)) continue;

                    length = perceptionValue.vector().get(1);
                    double angle = Math.toRadians((perceptionValue.vector().getFirst()));
                    double endX = (agent.getCoordinate().x() + Math.cos(angle) * length) * cellSize;
                    double endY = (agent.getCoordinate().y() + Math.sin(angle) * length) * cellSize;

                    rayLine = new Line(startX,startY, endX,endY);
                    rayLine.setStrokeWidth(3);
                    Color color;
                    switch (perceptionValue.type()){
                        case ALLY -> color = Color.GREEN;
                        case WALL -> color = Color.GRAY;
                        case TERRITORY -> color = Color.ORANGE;
                        case ALLY_FLAG -> color = Color.LIGHTGREEN;
                        case ENEMY -> color = Color.RED;
                        case ENEMY_FLAG -> color = Color.FIREBRICK;
                        default -> color = Color.BLACK;
                    }
                    rayLine.setStroke(color);
                    root.getChildren().add(rayLine);
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + perception.getClass());
        }
    }
}
