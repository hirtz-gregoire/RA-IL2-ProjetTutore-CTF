package engine;

import display.Display;
import engine.agent.Action;
import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.GameObject;

import java.time.Clock;
import java.util.*;
import java.util.stream.Collectors;

public class Engine {

    private List<Agent> agents;
    private GameMap map;
    private List<GameObject> objects;
    private Display display;
    private GameClock clock;

    private int tps = 20;
    private int actualTps = 0;

    public Engine(List<Agent> agents, GameMap map, List<GameObject> objects, Display display) {}
    public Engine(List<Agent> agents, GameMap map, List<GameObject> objects) {}

    public void run(){
        clock = new GameClock();
        long prevUpdate = -1;
        int updateCount = 0;

        while (true) {
            if((Math.floor(clock.millis()) / 1000.0) % 1 == 1) actualTps = updateCount;
            if(clock.millis() - prevUpdate < 1/tps) continue;

            prevUpdate = clock.millis();
            updateCount++;
            next();
        }
    }

    public void next(){
        /*
        1. recuperer action de chaque agent
        2. dans un ordre aléatoire
            - prend l'action de l'agent
            - la simuler
            - si probleme de collision (mur, joeur allié)
                - resoudre (appliquer un autre vecteur de correction, ou modifier le 1er vecteur)
            - appliquer l'action valide
            - check autre (objet (flag), etc)
        3. check fin simulation
        4. update affichage
        */

        List<Agent> newAgents = new ArrayList<>();
        List<GameObject> newObjects = new ArrayList<>(objects);

        var actions = fetchActions();
        var agentsCopy = new LinkedList<>(agents);
        Collections.shuffle(agentsCopy);

        while (!agentsCopy.isEmpty()) {
            var agent = agentsCopy.removeFirst();
            var action = actions.get(agent);
            executeAction(agent, action, map, newAgents, newObjects);
        }

        var isGameFinished = isGameFinished();

        agents = newAgents;
        objects = newObjects;

        display.update(map, agents, objects);
    }

    private Map<Agent, Action> fetchActions() {
        return this.agents.stream().parallel().collect(Collectors.toMap(agent -> agent,agent -> agent.getAction(this.map,this.agents,this.objects)));
    }

    private void executeAction(Agent agent, Action action, GameMap map, List<Agent> agents, List<GameObject> objects) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void collisions(Agent agent, GameMap map, List<Agent> agents, List<GameObject> objects) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private boolean isGameFinished() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}