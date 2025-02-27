package ia.perception;

import engine.Team;
import engine.Vector2;
import engine.agent.Agent;
import engine.map.Cell;
import engine.map.GameMap;
import engine.object.GameObject;

import java.util.ArrayList;
import java.util.List;

public class TerritoryCompass extends Compass {

    private final int maxAngle = 360;
    private double maxDistanceVision;
    public static int numberOfPerceptionsValuesNormalise = 3;

    public TerritoryCompass(Agent a, Filter filter) {
        super(a, filter);
    }

    @Override
    public void updatePerceptionValues(GameMap map, List<Agent> agents, List<GameObject> gameObjects) {
        List<Cell> cells = new ArrayList<>();
        for (List<Cell> cellList : map.getCells()) {
            for (Cell cell : cellList) {
                cells.add(cell);
            }
        }

        Cell nearest_cell = filter.nearestCell(my_agent,map.getCells());

        // Closest point to the agent
        var agentCoord = my_agent.getCoordinate();
        var cellCoord = nearest_cell.getCoordinate();
        Vector2 clipPosition = new Vector2(
                Math.clamp(agentCoord.x(), cellCoord.x(), cellCoord.x() + 1),
                Math.clamp(agentCoord.y(), cellCoord.y(), cellCoord.y() + 1)
        );

        // Time-to-reach the flag : d/(d/s) = s
        Vector2 vect = clipPosition.subtract(getMy_agent().getCoordinate());
        double time = vect.length() / (getMy_agent().getSpeed() + 0.00000001f);
        if(time == 0.0) {
            setPerceptionValues(List.of(
                    new PerceptionValue(
                            filter.getTeamMode()== Filter.TeamMode.ALLY ? PerceptionType.ALLY_TERRITORY:PerceptionType.ENEMY_TERRITORY,
                            List.of(0.0, 0.0)
                    )
            ));
            return;
        }
        double theta = normalisation(vect.normalized().getAngle() - getMy_agent().getAngular_position());

        setPerceptionValues(List.of(
                new PerceptionValue(
                        filter.getTeamMode()== Filter.TeamMode.ALLY ? PerceptionType.ALLY_TERRITORY:PerceptionType.ENEMY_TERRITORY,
                        List.of(theta, time)
                )
        ));
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

    @Override
    public List<Double> getPerceptionsValuesNormalise() {
        List<Double> perceptionsValues = getPerceptionValues().getFirst().vector();
        List<Double> perceptionsValuesNormalise = new ArrayList<>();
        perceptionsValuesNormalise.add(Math.cos(perceptionsValues.get(0)));
        perceptionsValuesNormalise.add(Math.sin(perceptionsValues.get(0)));

        if (perceptionsValuesNormalise.get(1) > maxDistanceVision)
            perceptionsValuesNormalise.add(1.0);
        else
            perceptionsValuesNormalise.add(perceptionsValues.get(1)/maxDistanceVision);

        return perceptionsValuesNormalise;
    }

    @Override
    public int getNumberOfPerceptionsValuesNormalise() {
        return numberOfPerceptionsValuesNormalise;
    }
}
