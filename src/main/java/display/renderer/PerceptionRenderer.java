package display.renderer;

import engine.agent.Agent;
import ia.perception.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;

import java.util.List;
import java.util.Map;

public class PerceptionRenderer {
    /**
     * Static method to display a perception
     *
     * @param perception         The perception to display
     * @param root               The pane on which display
     * @param cellSize           Size of a cell of the grid
     * @param desiredPerceptions
     */
    public static void render(Perception perception, Pane root, double cellSize, Map<PerceptionType, Boolean> desiredPerceptions) {
        Agent agent = perception.getMy_agent();

        List<PerceptionValue> perceptionValues = perception.getPerceptionValues().stream()
                .filter(pv -> desiredPerceptions.get(pv.type())).toList();
        switch (perception) {
            case AgentCompass _, NearestAgentCompass _, NearestEnemyFlagCompass _, NearestAllyFlagCompass _, ObjectCompass _, TerritoryCompass _, WallCompass _ -> {
                for(PerceptionValue perceptionValue : perceptionValues){
                    if(perceptionValue.vector().contains(Double.NaN)) continue;
                    double startX = agent.getCoordinate().x() * cellSize;
                    double startY = agent.getCoordinate().y() * cellSize;
                    double angle = Math.toRadians((perceptionValue.vector().getFirst()+agent.getAngular_position()));
                    double length = 1;
                    double endX = (agent.getCoordinate().x() + Math.cos(angle) * length) * cellSize;
                    double endY = (agent.getCoordinate().y() + Math.sin(angle) * length) * cellSize;
                    Line compassLine = drawPerceptionLine(startX, startY, endX, endY, perceptionValue, 0.4, 3);
                    root.getChildren().add(compassLine);
                }
            }
            case PerceptionRaycast _ -> {
                Line rayLine;
                PerceptionRaycast raycast = (PerceptionRaycast) perception;

                double startX = agent.getCoordinate().x() * cellSize;
                double startY = agent.getCoordinate().y() * cellSize;
                double[] raySizes = raycast.getRaySize();

                for (int j = 0; j < perceptionValues.size(); j++) {
                    PerceptionValue perceptionValue = perceptionValues.get(j);
                    if (perceptionValue.vector().contains(Double.NaN))
                        continue;

                    double length = perceptionValue.vector().get(1) * raySizes[j];
                    double angle = Math.toRadians(perceptionValue.vector().getFirst()+agent.getAngular_position());
                    double endX = (agent.getCoordinate().x() + Math.cos(angle) * length) * cellSize;
                    double endY = (agent.getCoordinate().y() + Math.sin(angle) * length) * cellSize;

                    rayLine = drawPerceptionLine( startX, startY, endX, endY, perceptionValue, Math.min(1.0/ raycast.getRayCount(),0.3), 2);
                    root.getChildren().add(rayLine);
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + perception.getClass());
        }
    }

    private static Line drawPerceptionLine(double startX, double startY, double endX, double endY, PerceptionValue perceptionValue, double fadeRate, double strokeWidth) {
        Line percpetionLine;
        percpetionLine = new Line(startX, startY, endX, endY);
        percpetionLine.setStrokeWidth(strokeWidth);
        Paint color;
        switch (perceptionValue.type()) {
            case ALLY -> color = Color.GREEN;
            case WALL -> color = Color.LIGHTGRAY;
            case ALLY_TERRITORY, ENEMY_TERRITORY -> color = Color.ORANGE;
            case ALLY_FLAG -> color = Color.LIGHTGREEN;
            case ENEMY -> color = Color.RED;
            case ENEMY_FLAG -> color = Color.FIREBRICK;
            default -> {
                color = new LinearGradient(
                        startX > endX ? 1 : 0,
                        startY > endY ? 1 : 0,
                        startX > endX ? 0 : 1,
                        startY > endY ? 0 : 1, true, CycleMethod.NO_CYCLE,
                        new Stop(0, new Color(0,0,0,fadeRate)),
                        new Stop(0.7, new Color(0,0,0,0.4)));
            }
        }
        percpetionLine.setStroke(color);
        return percpetionLine;
    }
}
