package ia.perception;

import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.GameObject;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.atan2;

public class ObjectCompass extends Perception{
    private final GameObject object_followed;
    private final PerceptionType return_type;

    public ObjectCompass(Agent a,GameObject followed,PerceptionType type) {
        super(a);
        this.object_followed = followed;
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

        //calcul temps
        double x = object_followed.getCoordinate().x() - getMy_agent().getCoordinate().x();
        double y = object_followed.getCoordinate().y() - getMy_agent().getCoordinate().y();
        double distance = Math.sqrt((x * x) + (y * y));
        //normalized x and y
        double norm_x = x/distance;
        double norm_y = y/distance;
        // Time-to-reach the flag : d/(d/s) = s
        double time = distance / getMy_agent().getSpeed();

        double goal = Math.toDegrees(Math.atan2(norm_y, norm_x));
        double theta_agent = getMy_agent().getAngular_position();
        double theta = normalisation(goal - theta_agent);

        ArrayList<Double> vector = new ArrayList<>();
        vector.add(theta);
        vector.add(time);

        setPerceptionValues(List.of(new PerceptionValue(return_type, vector)));
    }

    private double normalisation(double angle) {
        while (angle > 180) angle -= 360;
        while (angle < -180) angle += 360;
        return angle;
    }
}
