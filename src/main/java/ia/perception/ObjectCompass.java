package ia.perception;

import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.GameObject;

import java.util.ArrayList;
import java.util.List;

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
    public PerceptionValue getValue(GameMap map, List<Agent> agents, List<GameObject> gameObjects) {

        //calcul temps
        double x = getMy_agent().getCoordinate().x() - object_followed.getCoordinate().x();
        double y = getMy_agent().getCoordinate().y() - object_followed.getCoordinate().y();
        double distance = Math.sqrt((x * x) + (y * y));
        double temps;
        if(getMy_agent().getSpeed() == 0){
            temps = Double.MIN_VALUE;
        }
        temps = distance / getMy_agent().getSpeed();
        //calcul theta
        double theta = Math.toDegrees(Math.atan(y / x));

        ArrayList<Double> vector = new ArrayList<>();
        vector.add(theta);
        vector.add(temps);

        return new PerceptionValue(return_type, vector);
    }
}
