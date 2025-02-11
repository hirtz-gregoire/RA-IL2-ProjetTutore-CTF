package ia.evaluationFunctions;

import engine.Engine;
import engine.Team;
import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.Flag;
import engine.object.GameObject;
import ia.perception.TerritoryCompass;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DistanceEval extends EvaluationFunction {

    private final Map<Team, Map<Flag, Double>> agentClosestToFlag = new HashMap<>();
    private final Map<Team, Map<Flag, Double>> flagClosestToTerritory = new HashMap<>();
    private final Map<Team, Boolean> haveAgentBeenKilled = new HashMap<>();

    // Re-using some code..
    private static final TerritoryCompass compass = new TerritoryCompass(null, Team.NEUTRAL);

    public DistanceEval(Team targetTeam) {
        super(targetTeam);
    }

    @Override
    public void compute(Engine engine, GameMap map, List<Agent> agents, List<GameObject> objects) {
        for(Agent agent : agents) {
            if(!agent.isInGame()) {
                haveAgentBeenKilled.put(agent.getTeam(), true);
                continue;
            }

            for(GameObject object : objects) {
                if(object instanceof Flag flag) {
                    if(flag.getTeam() == agent.getTeam()) continue;

                    var distance = agent.getCoordinate().distance(object.getCoordinate());
                    updateClosest(agent.getTeam(), flag, agentClosestToFlag, distance);
                }
            }

            if(agent.getFlag().isPresent()) {
                var flag = agent.getFlag().get();

                compass.setTerritory_observed(agent.getTeam());
                var cell = compass.nearestCell(map.getCells());
                var distance = agent.getCoordinate().distance(cell.getCoordinate().add(0.5));

                updateClosest(agent.getTeam(), flag, flagClosestToTerritory, distance);
            }
        }
    }

    @Override
    public double result() {
        agentClosestToFlag.clear();
        flagClosestToTerritory.clear();
        haveAgentBeenKilled.clear();
        return 0;
    }

    private void updateClosest(Team team, Flag flag, Map<Team, Map<Flag, Double>> map, double distance) {
        map.computeIfAbsent(team, _ -> new HashMap<>())
                .compute(flag, (_, oldValue) -> oldValue == null
                        ? distance
                        : Math.max(oldValue, distance));
    }
}
