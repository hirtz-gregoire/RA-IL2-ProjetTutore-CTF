package ia.evaluationFunctions;

import engine.Engine;
import engine.Team;
import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.Flag;
import engine.object.GameObject;
import ia.model.NeuralNetworks.NeuralNetwork;
import ia.perception.TerritoryCompass;

import java.util.*;

public class DistanceEval extends EvaluationFunction {

    private static final double BIG_NUMBER = 100_000;
    private static final double ALLY_WEIGHT = 1;
    private static final double ENEMY_WEIGHT = 0.3;
    private static final double ALLY_KILL_WEIGHT = 0.00;
    private static final double ENEMY_KILL_WEIGHT = 0.00;
    private static final double L2_WEIGHT = 0.0001;

    private final Map<Team, Map<Flag, Double>> agentClosestToFlag = new HashMap<>();
    private final Map<Team, Map<Flag, Double>> flagClosestToTerritory = new HashMap<>();
    private final Map<Team, Set<Agent>> killedAgents = new HashMap<>();

    // Re-using some code..
    private static final Agent fakeAgent = new Agent();
    private static final TerritoryCompass compass = new TerritoryCompass(fakeAgent, Team.NEUTRAL);

    public DistanceEval(Team targetTeam) {
        super(targetTeam);
    }

    @Override
    public void update(Engine engine, GameMap map, List<Agent> agents, List<GameObject> objects) {
        for(Agent agent : agents) {
            if(!agent.isInGame() && agent.getRespawnTimer() > 0) {
                killedAgents.computeIfAbsent(agent.getTeam(), _ -> new HashSet<>()).add(agent);
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
                fakeAgent.setCoordinate(flag.getCoordinate());
                var cell = compass.nearestCell(map.getCells());
                var distance = flag.getCoordinate().distance(cell.getCoordinate().add(0.5));

                updateClosest(agent.getTeam(), flag, flagClosestToTerritory, distance);
            }
        }
    }

    @Override
    public double result(Engine engine, GameMap map, List<Agent> agents, List<GameObject> objects) {
        double allyScore = 0;
        double enemyScore = 0;
        int enemyCount = 0;
        int killedAllies = 0;
        int killedEnemies = 0;
        for(Team team : agentClosestToFlag.keySet()) {
            // ----------------- Team score
            double teamScore = 0;
            Set<Flag> flags = agentClosestToFlag.get(team).keySet();

            for(Double distance : agentClosestToFlag.get(team).values()) {
                teamScore += distance;
            }

            for(Flag flag : flags) {
                if(flag.getTeam() == team) continue;
                teamScore += flagClosestToTerritory
                        .computeIfAbsent(team, _ -> new HashMap<>())
                        .computeIfAbsent(flag, _ -> {
                    compass.setTerritory_observed(team);
                    fakeAgent.setCoordinate(flag.getCoordinate());
                    var cell = compass.nearestCell(map.getCells());
                    return flag.getCoordinate().distance(cell.getCoordinate().add(0.5));
                });
            }

            // We use a big number so that the score increase instead of decrease
            teamScore = BIG_NUMBER - teamScore;
            
            // ----------------- Kills
            var killCount = killedAgents.computeIfAbsent(team, _ -> new HashSet<>()).size();

            // ----------------- Global score
            if(team == targetTeam) {
                allyScore += teamScore;
                killedAllies += killCount;
            }
            else {
                killedEnemies += killCount;
                enemyScore += teamScore;
                enemyCount++;
            }
        }

        // L2 regularization
        double l2Total = 0;
        int allyCount = 0;
        for(Agent agent : agents) {
            if(agent.getTeam() != targetTeam) continue;
            if(agent.getModel() instanceof NeuralNetwork network) {
                for(double weight : network.getWeights()) {
                    l2Total += weight * weight;
                }
                allyCount++;
            }
        }
        if(allyCount > 0) {
            l2Total /= allyCount;
        }
        
        killedEnemies /= enemyCount;
        enemyScore /= enemyCount;
        
        double finalScore = allyScore * ALLY_WEIGHT - enemyScore * ENEMY_WEIGHT;
        finalScore -= l2Total * L2_WEIGHT;
        finalScore -= killedAllies * BIG_NUMBER * ALLY_KILL_WEIGHT;
        finalScore += killedEnemies * BIG_NUMBER * ENEMY_KILL_WEIGHT;

        agentClosestToFlag.clear();
        flagClosestToTerritory.clear();
        killedAgents.clear();

        return finalScore;
    }

    private void updateClosest(Team team, Flag flag, Map<Team, Map<Flag, Double>> map, double distance) {
        map.computeIfAbsent(team, _ -> new HashMap<>())
                .compute(flag, (_, oldValue) -> oldValue == null
                        ? distance
                        : Math.min(oldValue, distance));
    }
}
