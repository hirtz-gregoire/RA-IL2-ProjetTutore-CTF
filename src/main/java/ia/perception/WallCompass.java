package ia.perception;

import engine.Vector2;
import engine.agent.Agent;
import engine.map.Cell;
import engine.map.GameMap;
import engine.object.GameObject;

import java.util.ArrayList;
import java.util.List;

public class WallCompass extends Compass {

    public WallCompass(Agent a, Filter filter) {
        super(a, filter);
    }

    public static int numberOfPerceptionsValuesNormalise = 5;
    private double maxDistanceVision;

    @Override
    public void updatePerceptionValues(GameMap map, List<Agent> agents, List<GameObject> gameObjects) {
        Cell nearest_cell = filter.nearestCell(my_agent,map.getCells());

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
        List<Double> perceptionsValues = getPerceptionValues().getFirst().vector();
        List<Double> perceptionsValuesNormalise = new ArrayList<>();
        perceptionsValuesNormalise.add(Math.cos(perceptionsValues.get(0)));
        perceptionsValuesNormalise.add(Math.sin(perceptionsValues.get(0)));

        if (perceptionsValuesNormalise.get(1) > maxDistanceVision)
            perceptionsValuesNormalise.add(1.0);
        else
            perceptionsValuesNormalise.add(perceptionsValues.get(1)/maxDistanceVision);

        perceptionsValuesNormalise.add(Math.cos(perceptionsValues.get(2)));
        perceptionsValuesNormalise.add(Math.sin(perceptionsValues.get(2)));
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
