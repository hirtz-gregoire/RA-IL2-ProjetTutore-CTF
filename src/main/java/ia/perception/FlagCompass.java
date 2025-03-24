package ia.perception;

import engine.Vector2;
import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.Flag;
import engine.object.GameObject;

import java.util.List;

public class FlagCompass extends Compass {
    private boolean ignoreHolded;
    private double maxDistanceVision;
    public static int numberOfPerceptionsValuesNormalise = 3;

    /**
     * Create a new FlagCompass object, using a given filter
     * @param agent The agent that own the compass
     * @param filter The filter used to know wich flag to track
     * @param ignoreHolded Do the flag track flags carried by other agents
     */
    public FlagCompass(Agent agent, Filter filter, boolean ignoreHolded) {
        super(agent, filter);
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

        // Time-to-reach the flag : d/(d/s) = s
        Vector2 vect = nearest_flag.getCoordinate().subtract(getMy_agent().getCoordinate());
        double time = vect.length() / getMy_agent().getSpeed();
        double theta = vect.getAngle() - my_agent.getAngular_position();
        theta = (theta + 360) % 360;

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
    public double[] getPerceptionsValuesNormalise() {
        List<Double> perceptionsValues = getPerceptionValues().getFirst().vector();
        double[] perceptionsValuesNormalise = new double[numberOfPerceptionsValuesNormalise];

        var radiiAngle = Math.toRadians(perceptionsValues.get(0));
        perceptionsValuesNormalise[0] = normaliseIn180ToMinus180(radiiAngle);

        Double distance = perceptionsValues.get(1);
        if (distance > maxDistanceVision)
            perceptionsValuesNormalise[1] = 1.0;
        else {
            perceptionsValuesNormalise[1] = distance /maxDistanceVision;
        }

        perceptionsValuesNormalise[2] = perceptionsValues.get(2);
        return perceptionsValuesNormalise;
    }

    @Override
    public int getNumberOfPerceptionsValuesNormalise() {
        return numberOfPerceptionsValuesNormalise;
    }

    public boolean isIgnoreHolded() {
        return ignoreHolded;
    }
}
