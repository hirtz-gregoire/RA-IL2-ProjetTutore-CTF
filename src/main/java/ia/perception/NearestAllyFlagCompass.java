package ia.perception;

import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.Flag;
import engine.object.GameObject;

import java.util.ArrayList;
import java.util.List;

public class NearestAllyFlagCompass extends Perception {

    public NearestAllyFlagCompass(Agent a) {
        super(a);
    }

    /**
     * Computes the position and time-to-reach for the followed agent.
     * @param map map
     * @param agents list of agents
     * @param gameObjects list of objects
     * @return a Perception Value
     */
    public List<PerceptionValue> getValue(GameMap map, List<Agent> agents, List<GameObject> gameObjects) {
        List<Flag> filtered_flags = new ArrayList<Flag>();
        //filtering based on observed_team
        for (GameObject go : gameObjects){
            if (go instanceof Flag f){
                if (f.getTeam() == getMy_agent().getTeam()) {
                    filtered_flags.add(f);
                }
            }
        }
        if(filtered_flags.isEmpty()){
            List<Double> vector = new ArrayList<Double>();
            vector.add(0.0);
            vector.add(0.0);
            vector.add(0.0);
            return List.of(new PerceptionValue(PerceptionType.EMPTY, vector));
        }
        //nearest agent
        Flag nearest_flag = nearestFlag(filtered_flags);
        double x = nearest_flag.getCoordinate().x() - getMy_agent().getCoordinate().x();
        double y = nearest_flag.getCoordinate().y() - getMy_agent().getCoordinate().y();
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
        vector.add(theta);
        vector.add(time);
        return List.of(new PerceptionValue(PerceptionType.ENEMY_FLAG, vector));
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
        while (angle > 180) angle -= 360;
        while (angle < -180) angle += 360;
        return angle;
    }
}
