package engine;

import display.Display;
import engine.agent.Action;
import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.Object;

import java.util.List;
import java.util.Map;

public class Engine {

    private List<Agent> agents;
    private GameMap map;
    private List<Object> objects;
    private Display display;

    private int tps = 20;

    public Engine(List<Agent> agents, GameMap map, List<Object> objects, Display display) {}
    public Engine(List<Agent> agents, GameMap map, List<Object> objects) {}

    public void run(){
        /*
        loop tps
            next()
         */

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
    }

    private Map<Agent, Action> fetchActions() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void executeAction(Agent agent, Action action) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void isGameFinished() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}