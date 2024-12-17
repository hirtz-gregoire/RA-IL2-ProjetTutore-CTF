package ia.perception;

import engine.Team;
import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.GameObject;

import java.util.ArrayList;
import java.util.List;

public class AgentCompass extends Perception {
    private Agent agent_suivi;
    public AgentCompass(Agent a,Agent suivi) {
        super(a);
        this.agent_suivi = suivi;
    }


    @Override
    public PerceptionValue getValue(GameMap map, List<Agent> agents, List<GameObject> gameObjects) {

        //calcul temps
        double x = getMy_agent().getCoordinate().x() - agent_suivi.getCoordinate().x();
        double y = getMy_agent().getCoordinate().y() - agent_suivi.getCoordinate().y();
        double distance = Math.sqrt((x * x) + (y * y));
        double temps = distance / getMy_agent().getSpeed();
        //calcul theta
        double theta = Math.toDegrees(Math.atan(y / x));
        //
        ArrayList<Double> vector = new ArrayList<Double>();
        vector.add(theta);
        vector.add(temps);
        if (agent_suivi.getTeam() != getMy_agent().getTeam()){
            return new PerceptionValue(PerceptionType.ENEMY, vector);
        }
        return new PerceptionValue(PerceptionType.ALLY, vector);
    }
}
