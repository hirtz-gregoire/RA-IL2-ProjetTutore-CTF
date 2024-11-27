package engine.agent;

import engine.Coordinate;
import engine.Team;
import ia.model.Model;
import engine.map.GameMap;
import engine.object.Flag;
import engine.object.GameObject;

import java.util.List;
import java.util.Optional;

public class Agent {

    /** player coordinate */
    private Coordinate coordinate;

    /** rotation position*/
    private double angular_position;

    /** radius size */
    private double radius;

    /** maximum travel speed in forward */
    private int speed;

    /** maximum travel speed in reverse */
    private int backSpeed;

    /** maximum rotation speed */
    private int rotateSpeed;

    /** agent team reference */
    private Team team;

    /** decision making model */
    private Model model;

    /** reference Flag if has a flag otherwise null */
    private Optional<Flag> flag;

    /** agent living on the map or waiting to respawn */
    private boolean inGame = false;

    /** time remaining before respawn */
    private double respawnTime = 0;


    /**
     *
     * @param coord
     * @param radius
     * @param speed
     * @param backSpeed
     * @param rotateSpeed
     * @param team
     * @param flag
     * @param model
     *
     * @throws IllegalArgumentException if
     * <br>- coord==null
     * <br>- team==null
     * <br>- model==null
     * <br>- radius<0
     * <br>- speed<0
     * <br>- backSpeed<0
     * <br>- rotation
     */
    public Agent(Coordinate coord, double radius, int speed, int backSpeed, int rotateSpeed, Team team, Optional<Flag> flag, Model model) {

        // check object coord, team, and model are not null
        if (coord == null)
            throw new IllegalArgumentException("Coordinate cannot be null");
        if (team == null)
            throw new IllegalArgumentException("Team cannot be null");
        if (model == null)
            throw new IllegalArgumentException("Model cannot be null");

        // check radius not < 0
        // check radius, all speed are not < 0
        if (radius < 0)
            throw new IllegalArgumentException("Radius cannot be negative");
        if (speed < 0 || backSpeed < 0 || rotateSpeed<0)
            throw new IllegalArgumentException("Speed cannot be negative");

        this.coordinate = coord;
        this.radius = radius;
        this.speed = speed;
        this.backSpeed = backSpeed;
        this.team = team;
        this.flag = flag;
        this.model = model;
        this.angular_position = 0;
    }

    /**
     * call the model's getAction()
     * @param map GameMap
     * @param agents list Agent
     * @param objects list GameObject
     * @return
     */
    public Action getAction(GameMap map, List<Agent> agents, List<GameObject> objects){
        // application de getAction du models en attribut
        return model.getAction(map, agents, objects);
    }

    public Coordinate getCoordinate() { return coordinate; }
    public double getRadius() { return radius; }
    public double getAngular_position() {return angular_position;}
    public int getSpeed() { return speed; }
    public int getBackSpeed() { return backSpeed; }
    public int getRotateSpeed() { return rotateSpeed; }
    public Team getTeam() { return team; }
    public Model getModel() { return model; }
    public Optional<Flag> getFlag() { return flag; }
    public boolean isInGame() { return inGame; }
    public double getRespawnTime() { return respawnTime; }
    public void setCoordinate(Coordinate coord) { this.coordinate = coord; }
    public void setRespawnTimer(double respawnTimer) {this.respawnTime = respawnTimer;}
    public void setInGame(boolean inGame) {this.inGame = inGame;}
    public void setFlag(Optional<Flag> flag) {this.flag = flag;}
    public void setAngular_position(double angular_position) {this.angular_position = angular_position;}
}
