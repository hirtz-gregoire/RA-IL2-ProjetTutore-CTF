package ia.evaluationFunctions;

import engine.Engine;
import engine.Team;
import engine.agent.Agent;
import engine.map.DistanceBaker;
import engine.map.GameMap;
import engine.object.Flag;
import engine.object.GameObject;
import ia.perception.Filter;

import java.util.*;

public class AllyDistanceEval extends EvaluationFunction {

    private static final double TIME_WEIGHT = 1;

    private final Map<Flag, Double> agentClosestToFlag = new HashMap<>();
    private final Map<Flag, Double> flagClosestToTerritory = new HashMap<>();

    public AllyDistanceEval(Team targetTeam) {
        super(targetTeam);
    }

    @Override
    public void update(Engine engine, GameMap map, List<Agent> agents, List<GameObject> objects) {
        for(Agent agent : agents) {
            if(agent.getTeam() != targetTeam || !agent.isInGame()) {
                continue;
            }

            // Flag = compute how close we are to the base
            if(agent.getFlag().isPresent()) {
                var flag = agent.getFlag().get();
                agentClosestToFlag.put(flag, 0.0);
                var distance = DistanceBaker.computeDistance(agent.getCoordinate(), map, agent.getTeam());
                if(distance < 1.5) {
                    // Pre-computed values are not accurate enough when close to the goal
                    Filter filter = new Filter(Filter.TeamMode.ALLY, Filter.DistanceMode.NEAREST);
                    var cell = filter.nearestCell(flag, map.getCells());
                    var cellCoordinate = cell.getCoordinate();
                    var flagCoordinate = flag.getCoordinate();
                    distance = flagCoordinate.distance(
                            Math.clamp(flagCoordinate.x(), cellCoordinate.x(), cellCoordinate.x() + 1),
                            Math.clamp(flagCoordinate.y(), cellCoordinate.y(), cellCoordinate.y() + 1)
                    );
                }
                updateClosest(flag, flagClosestToTerritory, distance);
            }
            // No flag = compute how close we are to flags
            else {
                for(GameObject object : objects) {
                    if(object instanceof Flag flag) {
                        if(flag.getTeam() == agent.getTeam()) continue;
                        if(flag.getHolded()) continue;

                        var distance = DistanceBaker.computeDistance(agent.getCoordinate(), map, flag);
                        if(distance < 1.5) {
                            // Pre-computed values are not accurate enough when close to the goal
                            var flagCoordinate = flag.getCoordinate();
                            distance = flagCoordinate.distance(agent.getCoordinate());
                        }
                        updateClosest(flag, agentClosestToFlag, distance);
                    }
                }
            }
        }
    }

    @Override
    public double result(Engine engine, GameMap map, List<Agent> agents, List<GameObject> objects) {
        Team winningTeam = engine.isGameFinished();

        var totalDistance = 0.0;
        if(winningTeam != targetTeam) {
            for(Flag flag : agentClosestToFlag.keySet()) {
                totalDistance += agentClosestToFlag.get(flag);
                totalDistance += flagClosestToTerritory
                        .computeIfAbsent(flag, _ -> {
                            var distance = DistanceBaker.computeDistance(flag.getCoordinate(), map, targetTeam);
                            if(distance < 1.5) {
                                // Pre-computed values are not accurate enough when close to the goal
                                Filter filter = new Filter(Filter.TeamMode.ALLY, Filter.DistanceMode.NEAREST);
                                var cell = filter.nearestCell(flag, map.getCells());
                                var cellCoordinate = cell.getCoordinate();
                                var flagCoordinate = flag.getCoordinate();
                                distance = flagCoordinate.distance(
                                        Math.clamp(flagCoordinate.x(), cellCoordinate.x(), cellCoordinate.x() + 1),
                                        Math.clamp(flagCoordinate.y(), cellCoordinate.y(), cellCoordinate.y() + 1)
                                );
                            }
                            return distance;
                        });
            }
        }

        if(engine.getRemaining_turns() > 0) {
            double time = (double) engine.getRemaining_turns() / (double) engine.getMax_turns();
            totalDistance += (engine.isGameFinished() == targetTeam) ? (1 - time) * TIME_WEIGHT : time * TIME_WEIGHT;
        }

        return -totalDistance;
    }

    private void updateClosest(Flag flag, Map<Flag, Double> map, double distance) {
        map.compute(flag, (_, oldValue) -> oldValue == null
                ? distance
                : Math.min(oldValue, distance));
    }
}
