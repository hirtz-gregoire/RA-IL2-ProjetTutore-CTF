package ia.perception;

import engine.Vector2;
import engine.agent.Agent;
import engine.map.Cell;
import engine.map.GameMap;
import engine.object.GameObject;

import java.util.ArrayList;
import java.util.List;

public class TerritoryCompass extends Compass {

    private double maxDistanceVision;
    public static int numberOfPerceptionsValuesNormalise = 2;
    private static final List<Double> emptyPerception = List.of(0.0, 0.0);

    public TerritoryCompass(Agent a, Filter filter) {
        super(a, filter);
    }

    @Override
    public void updatePerceptionValues(GameMap map, List<Agent> agents, List<GameObject> gameObjects) {
        Cell nearest_cell = filter.nearestCell(my_agent, map.getCells());

        // Closest point to the agent
        var agentCoord = my_agent.getCoordinate();
        var cellCoord = nearest_cell.getCoordinate();
        Vector2 clipPosition = new Vector2(
                Math.clamp(agentCoord.x(), cellCoord.x(), cellCoord.x() + 1),
                Math.clamp(agentCoord.y(), cellCoord.y(), cellCoord.y() + 1)
        );

        // Time-to-reach the territory : d/(d/s) = s
        Vector2 vect = clipPosition.subtract(getMy_agent().getCoordinate());
        double time = vect.length() / getMy_agent().getSpeed();
        double theta = vect.getAngle() - my_agent.getAngular_position();
        theta = (theta + 360) % 360;

        if(time == 0.0) {
            setPerceptionValues(List.of(
                    new PerceptionValue(
                            filter.getTeamMode() == Filter.TeamMode.ALLY ? PerceptionType.ALLY_TERRITORY:PerceptionType.ENEMY_TERRITORY,
                            emptyPerception
                    )
            ));
            return;
        }

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
    public double[] getPerceptionsValuesNormalise() {
        List<Double> perceptionsValues = getPerceptionValues().getFirst().vector();
        double[] perceptionsValuesNormalise = new double[numberOfPerceptionsValuesNormalise];

        var radiiAngle = Math.toRadians(perceptionsValues.get(0));
        perceptionsValuesNormalise[0] = normaliseIn180ToMinus180(radiiAngle);

        Double distance = perceptionsValues.get(1);
        if (distance > maxDistanceVision)
            perceptionsValuesNormalise[1] = 1.0;
        else
            perceptionsValuesNormalise[1] = distance /maxDistanceVision;

        return perceptionsValuesNormalise;
    }

    @Override
    public int getNumberOfPerceptionsValuesNormalise() {
        return numberOfPerceptionsValuesNormalise;
    }
}
