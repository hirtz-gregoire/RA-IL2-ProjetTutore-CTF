package ia.perception;

import engine.Vector2;
import engine.agent.Agent;
import engine.map.Cell;
import engine.map.GameMap;
import engine.object.GameObject;

import java.util.ArrayList;
import java.util.List;

public class WallCompass extends Perception {

    public WallCompass(Agent a) {
        super(a);
    }

    public static int numberOfPerceptionsValuesNormalise = 3;
    private double maxDistanceVision;

    @Override
    public void updatePerceptionValues(GameMap map, List<Agent> agents, List<GameObject> gameObjects) {
        Cell nearest_cell = nearestCell(map.getCells());

        // Closest point to the agent
        var agentCoord = my_agent.getCoordinate();
        var cellCoord = nearest_cell.getCoordinate();
        Vector2 clipPosition = new Vector2(
                Math.clamp(agentCoord.x(), cellCoord.x(), cellCoord.x() + 1),
                Math.clamp(agentCoord.y(), cellCoord.y(), cellCoord.y() + 1)
        );

        // Time-to-reach the wall : d/(d/s) = s
        Vector2 vect = clipPosition.subtract(getMy_agent().getCoordinate());
        double time = vect.length() / (my_agent.getSpeed() + 0.00000001f);
        double theta = vect.normalized().getAngle();
        double normalized_theta = normalisation(vect.normalized().getAngle() - my_agent.getAngular_position());

        double normal;
        if (theta > -45 && theta < 45) { // RIGHT
            normal = 180;
        } else if (theta >= 135 || theta <= -135) { // LEFT
            normal = 0;
        } else if (theta >= 45) { // DOWN
            normal = 270;
        } else { // UP
            normal = 90;
        }
        normal = normalisation(normal - my_agent.getAngular_position());

        setPerceptionValues(List.of(
                new PerceptionValue(
                        PerceptionType.WALL,
                        List.of(normalized_theta, time, normal)
                )
        ));
    }

    @Override
    public List<Double> getPerceptionsValuesNormalise() {
        List<Double> perceptionsValuesNormalise = new ArrayList<>(getPerceptionValues().getFirst().vector());
        perceptionsValuesNormalise.set(0, perceptionsValuesNormalise.get(0)/maxAngle);
        if (perceptionsValuesNormalise.get(1) > maxDistanceVision)
            perceptionsValuesNormalise.set(1, 0.0);
        else
            perceptionsValuesNormalise.set(1, perceptionsValuesNormalise.get(1)/maxDistanceVision);

        perceptionsValuesNormalise.set(2, perceptionsValuesNormalise.get(2)/maxAngle);
        return perceptionsValuesNormalise;
    }

    @Override
    public int getNumberOfPerceptionsValuesNormalise() {
        return numberOfPerceptionsValuesNormalise;
    }

    /**
     * finding nearest cell of a specific team
     * @param cells list of all cells of the map
     * @return nearest agents from our agent
     */
    public Cell nearestCell(List<List<Cell>> cells) {
        int rows = cells.size();
        int cols = cells.getFirst().size();
        int centerX = (int)Math.floor(my_agent.getCoordinate().x());
        int centerY = (int)Math.floor(my_agent.getCoordinate().y());
        int maxRadius = Math.max(rows, cols);

        for (int r = 0; r < maxRadius; r++) {
            Cell closestCell = null;
            double closestDistance = Double.MAX_VALUE;

            for (int i = -r; i <= r; i++) {
                int x, y;

                // Top row
                x = centerX - r;
                y = centerY + i;
                if (isValid(x, y, rows, cols)) {
                    Cell cell = cells.get(x).get(y);
                    if(!cell.isWalkable()) {
                        double dist = cell.getCoordinate().add(0.5).distance(getMy_agent().getCoordinate());
                        if (dist < closestDistance) {
                            closestCell = cell;
                            closestDistance = dist;
                        }
                    }
                }

                // Bottom row
                x = centerX + r;
                y = centerY + i;
                if (isValid(x, y, rows, cols)) {
                    Cell cell = cells.get(x).get(y);
                    if(!cell.isWalkable()) {
                        double dist = cell.getCoordinate().add(0.5).distance(getMy_agent().getCoordinate());
                        if (dist < closestDistance) {
                            closestCell = cell;
                            closestDistance = dist;
                        }
                    }
                }

                // Left column (skip corners)
                x = centerX + i;
                y = centerY - r;
                if (isValid(x, y, rows, cols) && i != -r && i != r) {
                    Cell cell = cells.get(x).get(y);
                    if(!cell.isWalkable()) {
                        double dist = cell.getCoordinate().add(0.5).distance(getMy_agent().getCoordinate());
                        if (dist < closestDistance) {
                            closestCell = cell;
                            closestDistance = dist;
                        }
                    }
                }

                // Right column (skip corners)
                x = centerX + i;
                y = centerY + r;
                if (isValid(x, y, rows, cols) && i != -r && i != r) {
                    Cell cell = cells.get(x).get(y);
                    if(!cell.isWalkable()) {
                        double dist = cell.getCoordinate().add(0.5).distance(getMy_agent().getCoordinate());
                        if (dist < closestDistance) {
                            closestCell = cell;
                            closestDistance = dist;
                        }
                    }
                }
            }

            if(closestCell != null) return closestCell;
        }

        return null;
    }

    private static boolean isValid(int x, int y, int rows, int cols) {
        return x >= 0 && x < rows && y >= 0 && y < cols;
    }

    private double normalisation(double angle) {
        while (angle > 360) angle -= 360;
        while (angle < 0) angle += 360;
        return angle;
    }

    @Override
    public void setMy_agent(Agent my_agent) {
        super.setMy_agent(my_agent);
        maxDistanceVision = my_agent.getMaxDistanceVision();
    }
}
