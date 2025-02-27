package ia.perception;

import engine.Team;
import engine.Vector2;
import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.Flag;
import engine.object.GameObject;

import java.util.ArrayList;
import java.util.List;

public class FlagCompass extends Compass {
    private boolean ignoreHolded;
    private double maxDistanceVision;
    public static int numberOfPerceptionsValuesNormalise = 4;
    /**
     * constrcutor of NearestFlagCompass
     * @param a agent using this perception
     * @param t team observed
     */
    public FlagCompass(Agent a, Filter filter, boolean ignoreHolded) {
        super(a, filter);
        this.ignoreHolded = ignoreHolded;
    }

    /**
     * Computes the position and time-to-reach for the followed agent.
     * @param map map
     * @param agents list of agents
     * @param gameObjects list of objects
     */
    public void updatePerceptionValues(GameMap map, List<Agent> agents, List<GameObject> gameObjects) {
        List<Flag> filtered_flags = filter.filterByTeam(my_agent.getTeam(),gameObjects, Flag.class);
        //filtering based on observed_team
        filtered_flags = filter.customFilter(filtered_flags,Flag.class,o->{
            Flag flag = (Flag) o;
            if(!ignoreHolded) return flag;
            else if (!flag.getHolded()) {
                return flag;
            }
            return null;
        });

        if(filtered_flags.isEmpty()){
            //send back an empty value
            setPerceptionValues( List.of(new PerceptionValue(
                    PerceptionType.EMPTY,
                    List.of(0.0, 0.0, 1.0)
            )));
            return;
        }
        //nearest flag
        Flag nearest_flag = filter.filterByDistance(filtered_flags,my_agent, Flag.class).getFirst();

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

    @Override
    public void setMy_agent(Agent my_agent) {
       super.setMy_agent(my_agent);
       maxDistanceVision = my_agent.getMaxDistanceVision();
    }

    @Override
    public List<Double> getPerceptionsValuesNormalise() {
        List<Double> perceptionsValues = getPerceptionValues().getFirst().vector();
        List<Double> perceptionsValuesNormalise = new ArrayList<>();
        perceptionsValuesNormalise.add(Math.cos(perceptionsValues.get(0)));
        perceptionsValuesNormalise.add(Math.sin(perceptionsValues.get(0)));

        if (perceptionsValuesNormalise.get(1) > maxDistanceVision)
            perceptionsValuesNormalise.add(1.0);
        else
            perceptionsValuesNormalise.add(perceptionsValues.get(1)/maxDistanceVision);

        perceptionsValuesNormalise.add(perceptionsValues.get(2));
        return perceptionsValuesNormalise;
    }

    @Override
    public int getNumberOfPerceptionsValuesNormalise() {
        return numberOfPerceptionsValuesNormalise;
    }
}
