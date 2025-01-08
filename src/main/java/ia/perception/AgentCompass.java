package ia.perception;

import engine.Vector2;
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
    public List<PerceptionValue> getValue(GameMap map, List<Agent> agents, List<GameObject> gameObjects) {

        Vector2 vect = agent_suivi.getCoordinate().subtract(my_agent.getCoordinate());
        Vector2 norm = vect.normalized();

        // Time-to-reach the agent : d/(d/s) = s
        double time = vect.length() / getMy_agent().getSpeed();
        double theta = normalisation(norm.getAngle() - getMy_agent().getAngular_position());

        ArrayList<Double> vector = new ArrayList<>();
        vector.add(theta);
        vector.add(time);

        if (agent_suivi.getTeam() != getMy_agent().getTeam()){
            return List.of(new PerceptionValue(PerceptionType.ENEMY, vector));
        }
        return List.of(new PerceptionValue(PerceptionType.ALLY, vector));
    }

    private double normalisation(double angle) {
        while (angle > 360) angle -= 360;
        while (angle < 0) angle += 360;
        return angle;
    }
}
