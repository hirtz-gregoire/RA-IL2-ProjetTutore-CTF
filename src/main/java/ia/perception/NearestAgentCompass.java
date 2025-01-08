package ia.perception;

import engine.Team;
import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.GameObject;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.atan2;

public class NearestAgentCompass extends Perception{
    private Team observed_team;

    public NearestAgentCompass(Agent a,Team t) {
        super(a);
        observed_team = t;
    }
    /**
     * Computes the position and time-to-reach for the followed agent.
     * @param map map
     * @param agents list of agents
     * @param gameObjects list of objects
     * @return a Perception Value
     */
    public List<PerceptionValue> getValue(GameMap map, List<Agent> agents, List<GameObject> gameObjects) {
        //nearest agent
        Agent nearest_agent = nearestAgent(agents);
        //time
        double x = nearest_agent.getCoordinate().x() - getMy_agent().getCoordinate().x();
        double y = nearest_agent.getCoordinate().y() - getMy_agent().getCoordinate().y();
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
        vector.add(-theta);
        vector.add(time);

        if (observed_team != getMy_agent().getTeam()){
            return List.of(new PerceptionValue(PerceptionType.ENEMY, vector));
        }
        return List.of(new PerceptionValue(PerceptionType.ALLY, vector));
    }

    /**
     * finding nearest agent of a specific team
     * @param agents list of agents
     * @return nearest agents from our agent
     */
    public Agent nearestAgent(List<Agent> agents){
        //filtering based on observed_team
        List<Agent> filtered_agents = new ArrayList<>();
        for (Agent a : agents){
            if (a.getTeam() != observed_team ){
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

    private double normalisation(double angle) {
        while (angle > 360) angle -= 360;
        while (angle < 0) angle += 360;
        return angle;
    }

    public void setObserved_team(Team t) {
        this.observed_team = t;
    }
}
