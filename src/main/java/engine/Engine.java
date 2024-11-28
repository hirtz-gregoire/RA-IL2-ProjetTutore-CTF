package engine;

import display.Display;
import engine.agent.Action;
import engine.agent.Agent;
import engine.map.Cell;
import engine.map.GameMap;
import engine.map.SpawningCell;
import engine.object.Flag;
import engine.object.GameObject;
import javafx.application.Platform;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class Engine {
    private List<Agent> agents;
    private GameMap map;
    private List<GameObject> objects;
    private Display display;
    private GameClock clock;
    private int respawnTime;
    private final AtomicBoolean isRendering = new AtomicBoolean(false);

    private int tps = 30;
    private int actualTps = 0;

    public Engine(List<Agent> agents, GameMap map, List<GameObject> objects, Display display, int respawnTime) {
        this.agents = agents;
        this.map = map;
        this.objects = objects;
        this.display = display;
        this.respawnTime = respawnTime;
    }
    public Engine(List<Agent> agents, GameMap map, List<GameObject> objects) {
        this.agents = agents;
        this.map = map;
        this.objects = objects;
        this.display = null;
    }

    public void run(){
        clock = new GameClock();
        long prevUpdate = -1;
        int updateCount = 0;

        // We only work in turns to ease the game-saving process
        while (true) {
            //Si les Tps sont à 0, c'est qu'on est en pause donc il n'y a pas d'affichage
            if(tps == 0) continue;
            if((Math.floor(clock.millis()) / 1000.0) % 1 == 1) actualTps = updateCount;
            if(clock.millis() - prevUpdate < 1000 / tps) continue;

            prevUpdate = clock.millis();
            updateCount++;

            next();

            if(isGameFinished()) break;
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

        // Spawn agents
        var spawningCells = map.getSpawningCells();
        Collections.shuffle(spawningCells);
        Map<SpawningCell, Boolean> spawningCellsUsage = new HashMap<>();
        for(var spawningCell : spawningCells) {
            spawningCellsUsage.put(spawningCell, false);
        }
        for(Agent agent : agents) {
            if(!agent.isInGame()) {
                agent.setRespawnTimer(agent.getRespawnTimer() - 1);
                if(agent.getRespawnTimer() <= 0) {
                    int i = 0;
                    boolean spawned = false;
                    while(i < spawningCells.size() && !spawned) {
                        if(!spawningCellsUsage.get(spawningCells.get(i)) && spawningCells.get(i).getTeam() == agent.getTeam()) {
                            agent.setCoordinate(new Coordinate(spawningCells.get(i).getCoordinate().x()+0.5, spawningCells.get(i).getCoordinate().y()+0.5));
                            agent.setInGame(true);
                            spawningCellsUsage.put(spawningCells.get(i), true);
                            spawned = true;
                            System.out.println("spawn : "+agent.getCoordinate()+" - "+agent.getFlag().isPresent());
                        }
                        i++;
                    }
                }
            }
        }
        // Actions
        var actions = fetchActions();
        var agentsCopy = new ArrayList<>(actions.entrySet());
        Collections.shuffle(agentsCopy);

        while (!agentsCopy.isEmpty()) {
            //Explication de code svp...
            var pair = agentsCopy.removeFirst();
            var agent = pair.getKey();
            var action = pair.getValue();
            executeAction(agent, action, map, agents, objects);
        }

        var isGameFinished = isGameFinished();

        // Check if we have a display and if the display is available
        if(display != null) {
            if (isRendering.compareAndSet(false, true)) {
                Platform.runLater(() -> {
                    try {
                        display.update(map, agents, objects);
                    } finally {
                        isRendering.set(false);
                    }
                });
            }
        }
    }

    private boolean isGameFinished() {
        return false;
    }

    private Map<Agent, Action> fetchActions() {
        return this.agents.stream()
                .parallel()
                .filter(Agent::isInGame)
                .collect(Collectors.toMap(
                        agent -> agent,
                        agent -> agent.getAction(this.map,this.agents,this.objects)
                ));
    }

    private void executeAction(Agent agent, Action action, GameMap map, List<Agent> agents, List<GameObject> objects) {
        double prev_angle = agent.getAngular_position();
        double new_angle = (prev_angle + (action.getRotationRatio() * agent.getRotateSpeed())) % 360;
        if (new_angle < 0) {
            new_angle += 360;
        }
        agent.setAngular_position(new_angle);

        double angle_in_radians = Math.toRadians(new_angle);

        double speed = action.getSpeedRatio() * ((action.getSpeedRatio() >= 0) ? agent.getSpeed() : agent.getBackSpeed());
        double dx = speed * Math.cos(angle_in_radians);
        double dy = speed * Math.sin(angle_in_radians);

        Coordinate currentCoordinate = agent.getCoordinate();
        double x_t = currentCoordinate.x() + dx;
        double y_t = currentCoordinate.y() + dy;
        agent.setCoordinate(new Coordinate(x_t,y_t));
        collisions(agent,map,agents,objects);
    }

    private void collisions(Agent agent, GameMap map, List<Agent> agents, List<GameObject> objects) {
        // Out of bounds
        if (agent.getCoordinate().x() < 0 || agent.getCoordinate().x() >= map.getCells().size()) {
            agent.setCoordinate(new Coordinate(
                    Math.min(Math.max(agent.getCoordinate().x(), 0), map.getCells().size() - 0.1f),
                    agent.getCoordinate().y()
            ));
        }
        if (agent.getCoordinate().y() < 0 || agent.getCoordinate().y() >= map.getCells().getFirst().size()) {
            agent.setCoordinate(new Coordinate(
                    agent.getCoordinate().x(),
                    Math.min(Math.max(agent.getCoordinate().y(), 0), map.getCells().getFirst().size() - 0.1f)
            ));
        }
        // Players collision
        for(Agent other : agents) {
            if(other.equals(agent) || !agent.isInGame() || !other.isInGame()) continue;
            //System.out.println(agent.getTeam()+" "+agent.getCoordinate());
            //System.out.println(other.getTeam()+" "+other.getCoordinate());
            checkAgentCollision(agent, other);
        }
        // Wall collision
        for(List<Cell> cells : map.getCells()) {
            for(Cell cell : cells) {
                checkWallCollision(cell, agent);
            }
        }
       for(GameObject go : objects){
           checkItemCollision(agent,go);
       }
        if(agent.getFlag().isPresent()){
            agent.getFlag().get().setCoordinate(new Coordinate(agent.getCoordinate().x(),agent.getCoordinate().y()));
        }
    }

    /**
     * Compute and apply the collision between two agents
     * @param agent
     * @param other
     */
    private void checkAgentCollision(Agent agent, Agent other) {
        // Distance between the two agents
        double squaredDistX = Math.pow(agent.getCoordinate().x() - other.getCoordinate().x(), 2);
        double squaredDistY = Math.pow(agent.getCoordinate().y() - other.getCoordinate().y(), 2);
        double collisionDistance = Math.sqrt(squaredDistX + squaredDistY);

        double radius = Math.max(agent.getRadius(), other.getRadius());
        //System.out.println(collisionDistance+" - "+radius);
        if(collisionDistance >= radius) return; // No collision !

        //System.out.println(agent.getTeam()+" "+other.getTeam());
        // Maybe we get a kill..
        if(agent.getTeam() != other.getTeam()) {
            /*
            System.out.println("Maybe we get a kill..");
            System.out.println((int)Math.floor(agent.getCoordinate().y())+" - "+(int)Math.floor(agent.getCoordinate().x()));
            System.out.println((int)Math.floor(other.getCoordinate().y())+" - "+(int)Math.floor(other.getCoordinate().x()));
             */
            boolean agentIsSafe = map.getCells()
                    .get((int)Math.floor(agent.getCoordinate().x()))
                    .get((int)Math.floor(agent.getCoordinate().y()))
                    .getTeam() == agent.getTeam();

            boolean otherIsSafe = map.getCells()
                    .get((int)Math.floor(other.getCoordinate().x()))
                    .get((int)Math.floor(other.getCoordinate().y()))
                    .getTeam() == other.getTeam();
            /*
            System.out.println(agentIsSafe+" - "+otherIsSafe);
            System.out.println(agent.getTeam()+" - "+other.getTeam());
             */

            if(!agentIsSafe) {
                agent.setInGame(false);
                agent.setRespawnTimer(respawnTime);
                if (agent.getFlag().isPresent()){
                    agent.getFlag().get().setHolded(false);
                    agent.setFlag(Optional.empty());
                    System.out.println("Agent : "+agent.getFlag().isPresent());
                }
            }
            if(!otherIsSafe) {
                other.setInGame(false);
                other.setRespawnTimer(respawnTime);
                if (other.getFlag().isPresent()){
                    other.getFlag().get().setHolded(false);
                    other.setFlag(Optional.empty());
                    System.out.println("Other : "+other.getFlag().isPresent());
                }
            }

            // kill -> no collision
            if(!agentIsSafe || !otherIsSafe) return;
        }

        // Same team OR both in their own territory -> push
        double overlap = radius - collisionDistance;
        Coordinate pushVector = getUnidirectionalPush(
                other.getCoordinate(),
                agent.getCoordinate(),
                overlap
        );
        agent.setCoordinate(new Coordinate(
                agent.getCoordinate().x() + pushVector.x()/2,
                agent.getCoordinate().y() + pushVector.y()/2
        ));
        other.setCoordinate(new Coordinate(
                other.getCoordinate().x() - pushVector.x()/2,
                other.getCoordinate().y() - pushVector.y()/2
        ));
    }

    /**
     * Compute and apply the collision of a given cell
     * @param cell
     * @param agent
     */
    private void checkWallCollision(Cell cell, Agent agent) {
        if(cell.isWalkable()) return;

        // Closest point of the cell from the agent
        double closestX = Math.clamp(agent.getCoordinate().x(), cell.getCoordinate().x(), cell.getCoordinate().x() + 1);
        double closestY = Math.clamp(agent.getCoordinate().y(), cell.getCoordinate().y(), cell.getCoordinate().y() + 1);

        // Distance between the point and the agent
        double squaredDistX = Math.pow(agent.getCoordinate().x() - closestX, 2);
        double squaredDistY = Math.pow(agent.getCoordinate().y() - closestY, 2);
        double collisionDistance = Math.sqrt(squaredDistX + squaredDistY);

        if(collisionDistance >= agent.getRadius()) return; // No collision !

        // Push logic
        double overlap = agent.getRadius() - collisionDistance;
        Coordinate pushVector = getUnidirectionalPush(
                new Coordinate(cell.getCoordinate().x() + 0.5, cell.getCoordinate().y() + 0.5),
                agent.getCoordinate(),
                overlap
                );
        agent.setCoordinate(new Coordinate(
                agent.getCoordinate().x() + pushVector.x(),
                agent.getCoordinate().y() + pushVector.y()
        ));
    }

    private void checkItemCollision(Agent agent,GameObject go){
        //check if there is a collision
        double distX = Math.pow(agent.getCoordinate().x() - go.getCoordinate().x(),2);
        double distY = Math.pow(agent.getCoordinate().y() - go.getCoordinate().y(),2);
        double distCollision = Math.sqrt(distX+distY);

        double radius = Math.max(agent.getRadius(),0.5);// 0.5 arbitrary value because we assume every object radius is one
        if(distCollision >= radius) return;

        switch (go) {
            case Flag f -> {
                if (agent.getTeam() == f.getTeam()){
                    return;
                }
                if(!agent.isInGame()){
                    return;
                }
                if(f.getHolded() || agent.getFlag().isPresent()){
                    return;
                }
                f.setHolded(true);
                agent.setFlag(Optional.of(f));
                System.out.println(agent+" - "+agent.getTeam()+" - "+agent.isInGame());
                System.out.println("flag : "+agent.getCoordinate()+" - "+f.getCoordinate());
            }
            default -> {
                System.err.println("error");
                //You shouldn't be here
            }
        }

    }

    /**
     * Method for computing the repulsion vector that starts from a static object to an other object
     * @param staticObject The position of the non-movable object
     * @param thingToPush The position of the object that will be pushed away
     * @param overlap The amount of overlap between the two objects
     * @return A vector describing the distance to move the thingToPush object to get rid of the overlap
     */

    private Coordinate getUnidirectionalPush(Coordinate staticObject, Coordinate thingToPush, double overlap) {
        double offsetX = thingToPush.x() - staticObject.x();
        double offsetY = thingToPush.y() - staticObject.y();
        double offsetMagnitude = Math.sqrt(offsetX * offsetX + offsetY * offsetY);
        double pushDirX = (offsetMagnitude != 0) ? offsetX/offsetMagnitude : 1;
        double pushDirY = (offsetMagnitude != 0) ? offsetY/offsetMagnitude : 0;
        return new Coordinate(pushDirX * overlap, pushDirY * overlap);
    }


    public void setTps(int tps) {
        this.tps = tps;
    }
    public int getTps() {
        return this.tps;
    }
    public void setActualTps(int tps) {
        this.actualTps = tps;
    }

    public int getActualTps() {
        return this.actualTps;
    }
}
