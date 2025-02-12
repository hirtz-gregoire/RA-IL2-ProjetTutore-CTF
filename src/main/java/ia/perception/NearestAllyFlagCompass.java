package ia.perception;

import engine.Team;
import engine.Vector2;
import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.Flag;
import engine.object.GameObject;

import java.util.ArrayList;
import java.util.List;

public class NearestAllyFlagCompass extends Perception{
    private Team observed_team;
    private boolean ignoreHolded;
    private double maxDistanceVision;
    public static int numberOfPerceptionsValuesNormalise = 3;

    /**
     * constrcutor of NearestFlagCompass
     * @param a agent using this perception
     * @param t team observed
     */
    public NearestAllyFlagCompass(Agent a, Team t, boolean ignoreHolded) {
        super(a);
        observed_team = t;
        this.ignoreHolded = ignoreHolded;
    }

    /**
     * Computes the position and time-to-reach for the followed agent.
     * @param map map
     * @param agents list of agents
     * @param gameObjects list of objects
     */
    public void updatePerceptionValues(GameMap map, List<Agent> agents, List<GameObject> gameObjects) {
        List<Flag> filtered_flags = new ArrayList<>();
        //filtering based on observed_team
        for (GameObject go : gameObjects){
            if (go instanceof Flag f){
                if (f.getTeam() == observed_team) {
                    if(!ignoreHolded) {
                        filtered_flags.add(f);
                    }
                    else if (!f.getHolded()) {
                        filtered_flags.add(f);
                    }
                }
            }
        }
        if(filtered_flags.isEmpty()){
            //send back an empty value
            setPerceptionValues( List.of(new PerceptionValue(
                    PerceptionType.EMPTY,
                    List.of(0.0, 0.0, 1.0)
            )));
            return;
        }
        //nearest flag
        Flag nearest_flag = nearestFlag(filtered_flags);

        Vector2 vect = nearest_flag.getCoordinate().subtract(getMy_agent().getCoordinate());
        // Time-to-reach the flag : d/(d/s) = s
        double time = vect.length() / getMy_agent().getSpeed();

        double theta = Vector2.fromAngle(my_agent.getAngular_position()).angle(vect);
        setPerceptionValues(
                List.of(
                        new PerceptionValue(
                                (nearest_flag.getTeam() == my_agent.getTeam())?PerceptionType.ALLY_FLAG:PerceptionType.ENEMY_FLAG,
                                List.of(theta, time, nearest_flag.getHolded() ? 1.0 : 0.0)
                        )
                )
        );
    }

    /**
     * finding nearest flag of a specific team
     * @param filtered_flags list of flags in the game
     * @return nearest agents from our agent
     */
    public Flag nearestFlag(List<Flag> filtered_flags){

        //Finding nearest
        Flag nearest = filtered_flags.getFirst();
        double distance = Double.MAX_VALUE;
        for (Flag near : filtered_flags){
            Vector2 vect = near.getCoordinate().subtract(getMy_agent().getCoordinate());
            double temp_distance = vect.length();
            if (temp_distance < distance){
                distance = temp_distance;
                nearest = near;
            }
        }
        return nearest;
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

    @Override
    public int getNumberOfPerceptionsValuesNormalise() {
        return numberOfPerceptionsValuesNormalise;
    }
}
