package ia.perception;

import engine.Team;
import engine.Vector2;
import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.GameObject;

import java.util.ArrayList;
import java.util.List;

public class AgentCompass extends Compass {
    private Team observed_team;
    private double maxDistanceVision;
    public static int numberOfPerceptionsValuesNormalise = 3;

    public AgentCompass(Agent a, Filter filter) {
        super(a, filter);
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
        var filteredAgents = filter.filterByTeam(my_agent.getTeam(), agents, Agent.class);
        Agent nearest_agent = filter.filterByDistance(filteredAgents,my_agent,Agent.class).getFirst();
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
        perceptionsValuesNormalise[0] = (Math.cos(perceptionsValues.get(0)));
        perceptionsValuesNormalise[1] = (Math.sin(perceptionsValues.get(0)));

        if (perceptionsValuesNormalise[1] > maxDistanceVision)
            perceptionsValuesNormalise[2] = 1.0;
        else
            perceptionsValuesNormalise[2] = perceptionsValues.get(1)/maxDistanceVision;

        return perceptionsValuesNormalise;
    }

    @Override
    public int getNumberOfPerceptionsValuesNormalise() {
        return numberOfPerceptionsValuesNormalise;
    }
}
