package ia.perception;

import engine.Team;
import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.GameObject;

import java.util.ArrayList;
import java.util.List;

public class NearestAgentCompass extends Perception{
    private final Team observed_team;

    public NearestAgentCompass(Agent a,Team t) {
        super(a);
        observed_team = t;
    }

    public PerceptionValue getValue(GameMap map, List<Agent> agents, List<GameObject> gameObjects) {
        //nearest agent
        Agent nearest_agent = nearestAgent(agents);
        //time
        double x = getMy_agent().getCoordinate().x() - nearest_agent.getCoordinate().x();
        double y = getMy_agent().getCoordinate().y() - nearest_agent.getCoordinate().y();
        //distance
        double distance = Math.sqrt((x * x) + (y * y));
        double temps;
        if(getMy_agent().getSpeed() == 0){
            temps = Double.MIN_VALUE;
        }
        temps = distance / getMy_agent().getSpeed();

        //theta
        double theta = Math.toDegrees(Math.atan(y / x));

        ArrayList<Double> vector = new ArrayList<Double>();
        vector.add(theta);
        vector.add(temps);

        if (observed_team != getMy_agent().getTeam()){
            return new PerceptionValue(PerceptionType.ENEMY, vector);
        }
        return new PerceptionValue(PerceptionType.ALLY, vector);
    }

    public Agent nearestAgent(List<Agent> agents){
        //filtering based on observed_team
        List<Agent> filtered_agents = new ArrayList<>();
        for (Agent a : agents){
            if (a.getTeam() == observed_team){
                filtered_agents.add(a);
            }
        }

        //Finding nearest
        Agent nearest = filtered_agents.getFirst();
        double distance = Double.MAX_VALUE;
        for (Agent near : filtered_agents){
            double x = getMy_agent().getCoordinate().x() - near.getCoordinate().x();
            double y = getMy_agent().getCoordinate().y() - near.getCoordinate().y();
            double temp_distance = Math.sqrt((x * x) + (y * y));
            if (temp_distance < distance){
                distance = temp_distance;
                nearest = near;
            }
        }
        return nearest;
    }
}
