package ia.evaluationFunctions;

import engine.Engine;
import engine.Team;
import engine.Vector2;
import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.Flag;
import engine.object.GameObject;
import ia.perception.TerritoryCompass;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DistanceEval extends EvaluationFunction {

    private final Map<Team, Map<Flag, Double>> agentClosestToFlag = new HashMap<>();
    private final Map<Team, Map<Flag, Double>> flagClosestToTerritory = new HashMap<>();
    private final Map<Team, Boolean> haveAgentBeenKilled = new HashMap<>();

    // Re-using some code..
    private static final Agent fakeAgent = new Agent();
    private static final TerritoryCompass compass = new TerritoryCompass(fakeAgent, Team.NEUTRAL);

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

                compass.setTerritory_observed(flag.getTeam());
                fakeAgent.setCoordinate(flag.getCoordinate());
                var cell = compass.nearestCell(map.getCells());
                var distance = flag.getCoordinate().distance(cell.getCoordinate().add(0.5));

                updateClosest(agent.getTeam(), flag, flagClosestToTerritory, distance);
            }
        }
    }

    @Override
    public double result(Engine engine, GameMap map, List<Agent> agents, List<GameObject> objects) {
        Set<Flag> flags = null;
        for(Team team : agentClosestToFlag.keySet()) {
            // ----------------- Team score
            double teamScore = 0;
            if(flags == null) flags = agentClosestToFlag.get(team).keySet();

            for(Double distance : agentClosestToFlag.get(team).values()) {
                teamScore += distance;
            }

            for(Flag flag : flags) {
                teamScore += flagClosestToTerritory.get(team).computeIfAbsent(flag, _ -> {
                    compass.setTerritory_observed(flag.getTeam());
                    fakeAgent.setCoordinate(flag.getCoordinate());
                    var cell = compass.nearestCell(map.getCells());
                    return flag.getCoordinate().distance(cell.getCoordinate().add(0.5));
                });
            }

            // We use a big number so that the score increase instead of decrease
            teamScore = BIG_NUMBER - teamScore;

            // ----------------- Global score
            
        }



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
