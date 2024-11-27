package engine;

import display.Display;
import engine.agent.Action;
import engine.agent.Agent;
import engine.map.Cell;
import engine.map.GameMap;
import engine.object.GameObject;
import javafx.application.Platform;

import java.util.*;
import java.util.stream.Collectors;

public class Engine {
    private List<Agent> agents;
    private GameMap map;
    private List<GameObject> objects;
    private Display display;
    private GameClock clock;
    private double respawnTime;

    private int tps = 20;
    private int actualTps = 0;

    public Engine(List<Agent> agents, GameMap map, List<GameObject> objects, Display display, double respawnTime) {
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
            if((Math.floor(clock.millis()) / 1000.0) % 1 == 1) actualTps = updateCount;
            if(clock.millis() - prevUpdate < 1/tps) continue;

            prevUpdate = clock.millis();
            updateCount++;
            Platform.runLater(this::next);

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
        var actions = fetchActions();
        var agentsCopy = new LinkedList<>(agents);
        Collections.shuffle(agentsCopy);

        while (!agentsCopy.isEmpty()) {
            var agent = agentsCopy.removeFirst();
            var action = actions.get(agent);
            executeAction(agent, action, map, agents, objects);
        }

        var isGameFinished = isGameFinished();

        if(display != null) {
            display.update(map, agents, objects);
        }
    }

    private boolean isGameFinished() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private Map<Agent, Action> fetchActions() {
        return this.agents.stream()
                .parallel()
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
        if (agent.getCoordinate().x() < 0 || agent.getCoordinate().x() >= map.getCells().getFirst().size()) {
            agent.setCoordinate(new Coordinate(
                    Math.min(Math.max(agent.getCoordinate().x(), 0), map.getCells().getFirst().size() - 0.1f),
                    agent.getCoordinate().y()
            ));
        }
        if (agent.getCoordinate().y() < 0 || agent.getCoordinate().y() >= map.getCells().size()) {
            agent.setCoordinate(new Coordinate(
                    agent.getCoordinate().x(),
                    Math.min(Math.max(agent.getCoordinate().y(), 0), map.getCells().size() - 0.1f)
            ));
        }

        // Players collision
        for(Agent other : agents) {
            if(other.equals(agent)) continue;
            checkAgentCollision(agent, other);
        }

        // Wall collision
        int row = 0;
        int column;
        for(List<Cell> cells : map.getCells()) {
            column = 0;
            for(Cell cell : cells) {
                column++;
                checkWallCollision(cell, new Coordinate(column, row), agent);
            }
            row++;
        }

        // TODO : item collision
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
        if(collisionDistance >= radius) return; // No collision !

        // Maybe we get a kill..
        if(agent.getTeam() != other.getTeam()) {
            boolean agentIsSafe = map.getCells()
                    .get((int)Math.floor(agent.getCoordinate().y()))
                    .get((int)Math.floor(agent.getCoordinate().x()))
                    .getTeam() == agent.getTeam();

            boolean otherIsSafe = map.getCells()
                    .get((int)Math.floor(agent.getCoordinate().y()))
                    .get((int)Math.floor(agent.getCoordinate().x()))
                    .getTeam() == other.getTeam();

            if(!agentIsSafe) {
                agent.setInGame(false);
                agent.setRespawnTimer(respawnTime);
            }
            if(!otherIsSafe) {
                other.setInGame(false);
                other.setRespawnTimer(respawnTime);
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
     * @param cellCoordinate
     * @param agent
     */
    private void checkWallCollision(Cell cell, Coordinate cellCoordinate, Agent agent) {
        if(cell.isWalkable()) return;

        // Closest point of the cell from the agent
        double closestX = Math.clamp(agent.getCoordinate().x(), cellCoordinate.x(), cellCoordinate.x() + 1);
        double closestY = Math.clamp(agent.getCoordinate().y(), cellCoordinate.y(), cellCoordinate.y() + 1);

        // Distance between the point and the agent
        double squaredDistX = Math.pow(agent.getCoordinate().x() - closestX, 2);
        double squaredDistY = Math.pow(agent.getCoordinate().y() - closestY, 2);
        double collisionDistance = Math.sqrt(squaredDistX + squaredDistY);

        if(collisionDistance >= agent.getRadius()) return; // No collision !

        // Push logic
        double overlap = agent.getRadius() - collisionDistance;
        Coordinate pushVector = getUnidirectionalPush(
                new Coordinate(cellCoordinate.x() + 0.5, cellCoordinate.y() + 0.5),
                agent.getCoordinate(),
                overlap
                );
        agent.setCoordinate(new Coordinate(
                agent.getCoordinate().x() + pushVector.x(),
                agent.getCoordinate().y() + pushVector.y()
        ));
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
}