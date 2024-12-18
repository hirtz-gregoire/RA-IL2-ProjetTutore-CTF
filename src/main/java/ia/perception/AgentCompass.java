package ia.perception;

import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.GameObject;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.atan2;

public class AgentCompass extends Perception {
    private final Agent agent_suivi;
    public AgentCompass(Agent a,Agent suivi) {
        super(a);
        this.agent_suivi = suivi;
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

        double x = agent_suivi.getCoordinate().x() - getMy_agent().getCoordinate().x();
        double y = agent_suivi.getCoordinate().y() - getMy_agent().getCoordinate().y();
        double distance = Math.sqrt((x * x) + (y * y));
        //normalized x and y
        double norm_x = x/distance;
        double norm_y = y/distance;
        // Time-to-reach the flag : d/(d/s) = s
        double time;
        time = distance / getMy_agent().getSpeed();

        //theta
        double theta = Math.toDegrees(atan2(norm_y,norm_x));
        if(theta < 0){
            theta = 360 + theta;
        }
        theta = theta / 360;

        ArrayList<Double> vector = new ArrayList<>();
        vector.add(theta);
        vector.add(time);

        if (agent_suivi.getTeam() != getMy_agent().getTeam()){
            return new PerceptionValue(PerceptionType.ENEMY, vector);
        }
        return new PerceptionValue(PerceptionType.ALLY, vector);
    }
}
