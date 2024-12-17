package ia.perception;

import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.GameObject;

import java.util.ArrayList;
import java.util.List;

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

        double x = getMy_agent().getCoordinate().x() - agent_suivi.getCoordinate().x();
        double y = getMy_agent().getCoordinate().y() - agent_suivi.getCoordinate().y();
        double distance = Math.sqrt((x * x) + (y * y));

        double temps;
        if(getMy_agent().getSpeed() == 0){
            temps = Double.MIN_VALUE;
        }
        temps = distance / getMy_agent().getSpeed();

        double theta = Math.toDegrees(Math.atan(y / x));

        ArrayList<Double> vector = new ArrayList<>();
        vector.add(theta);
        vector.add(temps);

        if (agent_suivi.getTeam() != getMy_agent().getTeam()){
            return new PerceptionValue(PerceptionType.ENEMY, vector);
        }
        return new PerceptionValue(PerceptionType.ALLY, vector);
    }
}
