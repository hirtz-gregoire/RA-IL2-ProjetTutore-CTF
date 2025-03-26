package ia.perception;

import engine.Vector2;
import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.GameObject;

import java.util.ArrayList;
import java.util.List;

public class ObjectTracker extends Perception {
    private final GameObject trackedObject;
    private final PerceptionType return_type;
    private double maxDistanceVision;
    public static int numberOfPerceptionsValuesNormalise = 2;

    public ObjectTracker(Agent a, GameObject followed, PerceptionType type) {
        super(a);
        this.trackedObject = followed;
        this.return_type = type;
    }

    /**
     * Computes the position and time-to-reach for the followed agent.
     * @param map map
     * @param agents list of agents
     * @param gameObjects list of objects
     * @return a Perception Value
     */
    @Override
    public void updatePerceptionValues(GameMap map, List<Agent> agents, List<GameObject> gameObjects) {
        // Time-to-reach the object : d/(d/s) = s
        Vector2 vect = trackedObject.getCoordinate().subtract(getMy_agent().getCoordinate());
        double time = vect.length() / getMy_agent().getSpeed();
        double theta = vect.getAngle() - my_agent.getAngular_position();
        theta = (theta + 360) % 360;

        ArrayList<Double> vector = new ArrayList<>();
        vector.add(theta);
        vector.add(time);

        setPerceptionValues(List.of(new PerceptionValue(return_type, vector)));
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

        perceptionsValuesNormalise[0] = normaliseIn180ToMinus180(perceptionsValues.get(0));

        Double distance = perceptionsValues.get(1);
        if (distance > maxDistanceVision)
            perceptionsValuesNormalise[1] = 1.0;
        else
            perceptionsValuesNormalise[1] = distance/maxDistanceVision;

        return perceptionsValuesNormalise;
    }

    @Override
    public int getNumberOfPerceptionsValuesNormalise() {
        return numberOfPerceptionsValuesNormalise;
    }
}
