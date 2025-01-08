package display.renderer;

import com.sun.prism.paint.Gradient;
import engine.agent.Agent;
import ia.perception.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;

import java.awt.*;
import java.util.List;

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
                double[] raySizes = raycast.getRaySize();


                List<PerceptionValue> perceptionValues = raycast.getPerceptionValues();
                for (int j = 0; j < perceptionValues.size(); j++) {
                    PerceptionValue perceptionValue = perceptionValues.get(j);
                    if (perceptionValue.vector().contains(Double.NaN))
                        continue;

                    double length = perceptionValue.vector().get(1) * raySizes[j];
                    double angle = Math.toRadians(perceptionValue.vector().getFirst()+agent.getAngular_position());
                    double endX = (agent.getCoordinate().x() + Math.cos(angle) * length) * cellSize;
                    double endY = (agent.getCoordinate().y() + Math.sin(angle) * length) * cellSize;

                    rayLine = new Line(startX, startY, endX, endY);
                    rayLine.setStrokeWidth(2);
                    Paint color;
                    switch (perceptionValue.type()) {
                        case ALLY -> color = Color.GREEN;
                        case WALL -> color = Color.LIGHTGRAY;
                        case TERRITORY -> color = Color.ORANGE;
                        case ALLY_FLAG -> color = Color.LIGHTGREEN;
                        case ENEMY -> color = Color.RED;
                        case ENEMY_FLAG -> color = Color.FIREBRICK;
                        default -> {
                            color = new LinearGradient(
                                    startX > endX ? 1 : 0,
                                    startY > endY ? 1 : 0,
                                    startX > endX ? 0 : 1,
                                    startY > endY ? 0 : 1, true, CycleMethod.NO_CYCLE,
                                    new Stop(0, new Color(0,0,0,Math.min(1.0/raycast.getRayCount(),0.3))),
                                    new Stop(0.7, new Color(0,0,0,0.4)));
                        }
                    }
                    rayLine.setStroke(color);
                    root.getChildren().add(rayLine);
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + perception.getClass());
        }
    }
}
