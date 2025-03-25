package ia.perception;

import engine.Vector2;
import engine.agent.Agent;
import engine.map.Cell;
import engine.map.GameMap;
import engine.object.GameObject;

import java.util.List;

public class WallCompass extends Compass {

    public WallCompass(Agent a, Filter filter) {
        super(a, filter);
    }

    public static int numberOfPerceptionsValuesNormalise = 3;
    private double maxDistanceVision;

    @Override
    public void updatePerceptionValues(GameMap map, List<Agent> agents, List<GameObject> gameObjects) {
        Cell nearest_cell = filter.nearestCell(my_agent,map.getCells());

        // Closest point on the cell to the agent
        var agentCoord = my_agent.getCoordinate();
        var cellCoord = nearest_cell.getCoordinate();
        Vector2 clipPosition = new Vector2(
                Math.clamp(agentCoord.x(), cellCoord.x(), cellCoord.x() + 1),
                Math.clamp(agentCoord.y(), cellCoord.y(), cellCoord.y() + 1)
        );

        // Time-to-reach the wall : d/(d/s) = s
        Vector2 vect = clipPosition.subtract(getMy_agent().getCoordinate());
        double time = vect.length() / getMy_agent().getSpeed();
        double theta = vect.getAngle() - my_agent.getAngular_position();
        theta = (theta + 360) % 360;

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
                        List.of(theta, time, normal)
                )
        ));
    }

    @Override
    public double[] getPerceptionsValuesNormalise() {
        List<Double> perceptionsValues = getPerceptionValues().getFirst().vector();
        double[] perceptionsValuesNormalise = new double[numberOfPerceptionsValuesNormalise];

        var radiiAngle = Math.toRadians(perceptionsValues.get(0));
        radiiAngle = normaliseIn180ToMinus180(radiiAngle);
        perceptionsValuesNormalise[0] = radiiAngle;

        Double distance = perceptionsValues.get(1);
        if (distance > maxDistanceVision)
            perceptionsValuesNormalise[1] = 1.0;
        else
            perceptionsValuesNormalise[1] = distance /maxDistanceVision;

        radiiAngle = Math.toRadians(perceptionsValues.get(2));
        perceptionsValuesNormalise[2] = normaliseIn180ToMinus180(radiiAngle);
        return perceptionsValuesNormalise;
    }

    @Override
    public int getNumberOfPerceptionsValuesNormalise() {
        return numberOfPerceptionsValuesNormalise;
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
