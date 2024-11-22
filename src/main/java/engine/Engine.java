package engine;

import display.Display;
import engine.agent.Agent;
import engine.map.Map;
import engine.object.Object;

import java.util.List;

public class Engine {

    private List<Agent> agents;
    private Map map;
    private List<Object> objects;
    private Display display;

    private int tps = 20;

    public Engine(List<Agent> agents, Map map, List<Object> objects, Display display) {}
    public Engine(List<Agent> agents, Map map, List<Object> objects) {}

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
}