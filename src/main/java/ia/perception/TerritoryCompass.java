package ia.perception;

import engine.Team;
import engine.Vector2;
import engine.agent.Agent;
import engine.map.Cell;
import engine.map.GameMap;
import engine.object.GameObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class TerritoryCompass extends Perception{

    private Team territory_observed;
    private final int maxAngle = 360;
    private double maxDistanceVision;
    public static int numberOfPerceptionsValuesNormalise = 2;

    public TerritoryCompass(Agent a,Team t) {
        super(a);
        territory_observed = t;
    }

    @Override
    public void updatePerceptionValues(GameMap map, List<Agent> agents, List<GameObject> gameObjects) {
        //nearest agent
        Cell nearest_cell = nearestCell(map.getCells());
        Vector2 nearest = nearest_cell.getCoordinate().add(0.5);
        Vector2 vect = nearest.subtract(getMy_agent().getCoordinate());

        // Time-to-reach the flag : d/(d/s) = s
        double time = vect.length() / getMy_agent().getSpeed() + 0.00000001f;
        double theta = normalisation(vect.normalized().getAngle() - getMy_agent().getAngular_position());

        setPerceptionValues(List.of(
                new PerceptionValue(
                        (territory_observed == my_agent.getTeam()) ? PerceptionType.ALLY_TERRITORY:PerceptionType.ENEMY_TERRITORY,
                        List.of(theta, time)
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
                    if(cell.getTeam() == territory_observed) {
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
                    if(cell.getTeam() == territory_observed) {
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
                    if(cell.getTeam() == territory_observed) {
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
                    if(cell.getTeam() == territory_observed) {
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

    public void setTerritory_observed(Team t) {
        this.territory_observed = t;
    }

    @Override
    public void setMy_agent(Agent my_agent) {
        super.setMy_agent(my_agent);
        maxDistanceVision = my_agent.getMaxDistanceVision();
    }

    @Override
    public List<Double> getPerceptionsValuesNormalise() {
        List<Double> perceptionsValuesNormalise = new ArrayList<>(getPerceptionValues().getFirst().vector());
        perceptionsValuesNormalise.set(0, perceptionsValuesNormalise.get(0)/maxAngle);
        if (perceptionsValuesNormalise.get(1) > maxDistanceVision)
            perceptionsValuesNormalise.set(1, 0.0);
        else
            perceptionsValuesNormalise.set(1, perceptionsValuesNormalise.get(1)/maxDistanceVision);
        return perceptionsValuesNormalise;
    }

    @Override
    public int getNumberOfPerceptionsValuesNormalise() {
        return numberOfPerceptionsValuesNormalise;
    }
}
