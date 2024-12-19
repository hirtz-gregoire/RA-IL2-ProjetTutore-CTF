package ia.perception;

import engine.Team;
import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.Flag;
import engine.object.GameObject;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.atan;
import static java.lang.Math.atan2;

public class NearestEnemyFlagCompass extends Perception{

    public NearestEnemyFlagCompass(Agent a) {
        super(a);
    }
    /**
     * Computes the position and time-to-reach for the followed agent.
     * @param map map
     * @param agents list of agents
     * @param gameObjects list of objects
     * @return a Perception Value
     */
    public PerceptionValue getValue(GameMap map, List<Agent> agents, List<GameObject> gameObjects) {
        //nearest agent
        Flag nearest_flag = nearestFlag(gameObjects);
        double x = nearest_flag.getCoordinate().x() - getMy_agent().getCoordinate().x();
        double y = nearest_flag.getCoordinate().y() - getMy_agent().getCoordinate().y();
        double distance = Math.sqrt((x * x) + (y * y));
        //normalized x and y
        double norm_x = x/distance;
        double norm_y = y/distance;
        // Time-to-reach the flag : d/(d/s) = s
        double time = distance / getMy_agent().getSpeed();

        double goal = Math.atan2(norm_y, norm_x);
        double theta_agent = Math.toRadians(getMy_agent().getAngular_position());
        double theta = normalisation(goal - theta_agent);

        ArrayList<Double> vector = new ArrayList<>();
        vector.add(theta);
        vector.add(time);
        return new PerceptionValue(PerceptionType.ENEMY_FLAG, vector);
    }

    /**
     * finding nearest flag of a specific team
     * @param gameObjects list of gameObjects
     * @return nearest agents from our agent
     */
    public Flag nearestFlag(List<GameObject> gameObjects){
        //filtering based on observed_team
        List<Flag> filtered_flags = new ArrayList<>();
        for (GameObject go : gameObjects){
            if (go instanceof Flag f){
                if (f.getTeam() != getMy_agent().getTeam()) {
                    filtered_flags.add(f);
                }
            }
        }

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
        while (angle > Math.PI) angle -= 2 * Math.PI;
        while (angle < -Math.PI) angle += 2 * Math.PI;
        return angle;
    }
}
