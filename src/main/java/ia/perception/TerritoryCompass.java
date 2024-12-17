package ia.perception;

import engine.Team;
import engine.agent.Agent;
import engine.map.Cell;
import engine.map.GameMap;
import engine.object.GameObject;

import java.util.ArrayList;
import java.util.List;

public class TerritoryCompass extends Perception{

    Team territory_observed;

    public TerritoryCompass(Agent a,Team t) {
        super(a);
        territory_observed = t;
    }

    @Override
    public PerceptionValue getValue(GameMap map, List<Agent> agents, List<GameObject> gameObjects) {
        //nearest agent
        Cell nearest_cell = nearestCell(map.getCells());
        //time
        double x = getMy_agent().getCoordinate().x() - nearest_cell.getCoordinate().x();
        double y = getMy_agent().getCoordinate().y() - nearest_cell.getCoordinate().y();
        //distance
        double distance = Math.sqrt((x * x) + (y * y));
        double temps;
        if(getMy_agent().getSpeed() == 0){
            temps = Double.MIN_VALUE;
        }
        temps = distance / getMy_agent().getSpeed();

        //theta
        double theta = Math.toDegrees(Math.atan(y / x));

        ArrayList<Double> vector = new ArrayList<>();
        vector.add(theta);
        vector.add(temps);

        return new PerceptionValue(PerceptionType.TERRITORY, vector);
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
}
