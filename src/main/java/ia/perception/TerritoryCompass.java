package ia.perception;

import engine.Team;
import engine.Vector2;
import engine.agent.Agent;
import engine.map.Cell;
import engine.map.GameMap;
import engine.object.GameObject;

import java.util.ArrayList;
import java.util.List;

public class TerritoryCompass extends Perception{

    private Team territory_observed;
    private final int maxAngle = 360;
    private double maxDistanceVision;

    public TerritoryCompass(Agent a,Team t) {
        super(a);
        territory_observed = t;
        this.numberOfPerceptionsValuesNormalise = 3;
    }

    @Override
    public void updatePerceptionValues(GameMap map, List<Agent> agents, List<GameObject> gameObjects) {
        //nearest agent
        Cell nearest_cell = nearestCell(map.getCells());
        Vector2 nearest = nearest_cell.getCoordinate().add(0.5);
        Vector2 vect = nearest.subtract(getMy_agent().getCoordinate());

        // Time-to-reach the flag : d/(d/s) = s
        double time = vect.length() / getMy_agent().getSpeed() + 0.00000001f;
        double theta = normalisation(vect.normalized().getAngle() - getMy_agent().getAngular_position());

        setPerceptionValues(List.of(
                new PerceptionValue(
                        (territory_observed == my_agent.getTeam()) ? PerceptionType.ALLY_TERRITORY:PerceptionType.ENEMY_TERRITORY,
                        List.of(theta, time)
                )
        ));
    }

    /**
     * finding nearest agent of a specific team
     * @param cells list of all cells of the map
     * @return nearest agents from our agent
     */
    public Cell nearestCell(List<List<Cell>> cells){
        //filtering based on observed_team
        List<Cell> filtered_cells = new ArrayList<>();
        for (List<Cell> l_c : cells){
            for (Cell c : l_c) {
                if (c.getTeam() == territory_observed) {
                    filtered_cells.add(c);
                }
            }
        }

        //Finding nearest
        Cell nearest = filtered_cells.getFirst();
        double distance = Double.MAX_VALUE;
        for (Cell near : filtered_cells){
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
        while (angle > 360) angle -= 360;
        while (angle < 0) angle += 360;
        return angle;
    }

    public void setTerritory_observed(Team t) {
        this.territory_observed = t;
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
