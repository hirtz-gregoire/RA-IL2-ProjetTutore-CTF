package ia.perception;

import engine.Team;
import engine.Vector2;
import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.GameObject;

import java.util.ArrayList;
import java.util.List;

public class NearestAgentCompass extends Perception{
    private Team observed_team;
    private double maxDistanceVision;
    public static int numberOfPerceptionsValuesNormalise = 2;

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
    public void updatePerceptionValues(GameMap map, List<Agent> agents, List<GameObject> gameObjects) {
        //nearest agent
        Agent nearest_agent = nearestAgent(agents);
        //time
        Vector2 vect = nearest_agent.getCoordinate().subtract(my_agent.getCoordinate());
        Vector2 norm = vect.normalized();

        // Time-to-reach the agent : d/(d/s) = s
        double time = vect.length() / getMy_agent().getSpeed();
        double theta = normalisation(norm.getAngle() - getMy_agent().getAngular_position());

        ArrayList<Double> vector = new ArrayList<>();
        vector.add(-theta);
        vector.add(time);

        if (observed_team != getMy_agent().getTeam()){
            setPerceptionValues(List.of(new PerceptionValue(PerceptionType.ENEMY, vector)));
            return;
        }
        setPerceptionValues(List.of(new PerceptionValue(PerceptionType.ALLY, vector)));
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

    @Override
    public void setMy_agent(Agent my_agent) {
        super.setMy_agent(my_agent);
        maxDistanceVision = my_agent.getMaxDistanceVision();
    }

    @Override
    public List<Double> getPerceptionsValuesNormalise() {
        List<Double> perceptionsValuesNormalise = new ArrayList<>(getPerceptionValues().getFirst().vector());
        perceptionsValuesNormalise.set(0, perceptionsValuesNormalise.get(0)/maxAngle);
        if (perceptionsValuesNormalise.get(1) > maxDistanceVision)
            perceptionsValuesNormalise.set(1, 0.0);
        else
            perceptionsValuesNormalise.set(1, perceptionsValuesNormalise.get(1)/maxDistanceVision);
        return perceptionsValuesNormalise;
    }
}
