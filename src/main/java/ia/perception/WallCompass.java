package ia.perception;

import engine.Team;
import engine.Vector2;
import engine.agent.Agent;
import engine.map.Cell;
import engine.map.GameMap;
import engine.object.GameObject;

import java.util.List;

public class WallCompass extends Perception {

    public WallCompass(Agent a) {
        super(a);
    }

    @Override
    public void updatePerceptionValues(GameMap map, List<Agent> agents, List<GameObject> gameObjects) {
        // Nearest wall
        Cell nearest_cell = nearestCell(map.getCells());
        Vector2 nearest = nearest_cell.getCoordinate().add(0.5);
        Vector2 vect = nearest.subtract(my_agent.getCoordinate());

        // Time-to-reach the wall : d/(d/s) = s
        double time = vect.length() / my_agent.getSpeed() + 0.00000001f;
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

        for (int r = 1; r < maxRadius; r++) {
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
                        double dist = cell.getCoordinate().add(0.5).subtract(getMy_agent().getCoordinate()).length();
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
                        double dist = cell.getCoordinate().add(0.5).subtract(getMy_agent().getCoordinate()).length();
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
                        double dist = cell.getCoordinate().add(0.5).subtract(getMy_agent().getCoordinate()).length();
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
                        double dist = cell.getCoordinate().add(0.5).subtract(getMy_agent().getCoordinate()).length();
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
}
