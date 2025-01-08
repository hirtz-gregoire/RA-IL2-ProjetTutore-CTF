package ia.perception;

import engine.Team;
import engine.Vector2;
import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.Flag;
import engine.object.GameObject;

import java.util.ArrayList;
import java.util.List;

public class NearestFlagCompass extends Perception{
    private Team observed_team;

    /**
     * constrcutor of NearestFlagCompass
     * @param a agent using this perception
     * @param t team observed
     */
    public NearestFlagCompass(Agent a,Team t) {
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
        List<Flag> filtered_flags = new ArrayList<>();
        //filtering based on observed_team
        for (GameObject go : gameObjects){
            if (go instanceof Flag f){
                if (f.getTeam() != observed_team) {
                    filtered_flags.add(f);
                }
            }
        }
        if(filtered_flags.isEmpty()){
            //send back an empty value
            List<Double> vector = new ArrayList<>();vector.add(0.0);vector.add(0.0);vector.add(0.0);return List.of(new PerceptionValue(PerceptionType.EMPTY, vector));
        }
        //nearest flag
        Flag nearest_flag = nearestFlag(filtered_flags);

        Vector2 vect = nearest_flag.getCoordinate().subtract(getMy_agent().getCoordinate());
        // Time-to-reach the flag : d/(d/s) = s
        double time = vect.length() / getMy_agent().getSpeed();
        double theta = normalisation(vect.normalized().getAngle() - getMy_agent().getAngular_position());

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
            Vector2 vect = near.getCoordinate().subtract(getMy_agent().getCoordinate());
            double temp_distance = vect.length();
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

    public void setObserved_team(Team t) {
        this.observed_team = t;
    }
}
