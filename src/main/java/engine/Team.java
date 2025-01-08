package engine;

import engine.agent.Agent;
import engine.map.Cell;
import engine.map.Ground;
import engine.map.SpawningCell;
import engine.map.Wall;
import engine.object.Flag;
import engine.object.GameObject;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public enum Team {
    NEUTRAL,
    BLUE,
    RED,
    GREEN,
    YELLOW,
    PURPLE,
    PINK,
    ORANGE,
    CYAN;

    /**
     * Convert any Team instance into the char equivalent
     * @param team Enum value to convert into char
     * @return Char representing enum value
     */
    public static char teamToChar(Team team) {
        char teamChar;
        switch (team){
            case Team.BLUE -> teamChar = '1';
            case Team.RED -> teamChar = '2';
            case Team.GREEN -> teamChar = '3';
            case Team.YELLOW -> teamChar = '4';
            case Team.PURPLE -> teamChar = '5';
            case Team.PINK -> teamChar = '6';
            case Team.ORANGE -> teamChar = '7';
            case Team.CYAN -> teamChar = '8';
            case Team.NEUTRAL -> teamChar = '0';
            default -> teamChar = '-';
        }
        return teamChar;
    }

    /**
     * Convert any char into the Team equivalent
     * @param teamChar Char to convert into a Team
     * @return Team corresponding to the given char
     */
    public static Team charToTeam(char teamChar) {
        Team team;
        switch (teamChar){
            case '1'-> team = Team.BLUE;
            case '2'-> team = Team.RED;
            case '3'-> team = Team.GREEN;
            case '4'-> team = Team.YELLOW;
            case '5'-> team = Team.PURPLE;
            case '6'-> team = Team.PINK;
            case '7'-> team = Team.ORANGE;
            case '8'-> team = Team.CYAN;
            default -> team = Team.NEUTRAL;
        }
        return team;
    }

    /**
     * Convert any int into the Team equivalent
     * @param numEquipe int to convert into a Team
     * @return Team corresponding to the given int
     */
    public static Team numEquipeToTeam(int numEquipe) {
        Team team;
        switch (numEquipe){
            case 1 -> team = Team.BLUE;
            case 2 -> team = Team.RED;
            case 3 -> team = Team.GREEN;
            case 4 -> team = Team.YELLOW;
            case 5 -> team = Team.PURPLE;
            case 6 -> team = Team.PINK;
            case 7 -> team = Team.ORANGE;
            case 8 -> team = Team.CYAN;
            default -> team = Team.NEUTRAL;
        }
        return team;
    }

    public static Color TeamToColor(Team team) {
        Color color;
        switch (team){
            case BLUE -> color =  Color.BLUE;
            case RED -> color = Color.RED;
            case GREEN -> color =  Color.GREEN;
            case YELLOW -> color = Color.YELLOW;
            case PURPLE -> color =  Color.PURPLE;
            case PINK -> color = Color.PINK;
            case ORANGE -> color =  Color.ORANGE;
            case CYAN -> color = Color.CYAN;
            default -> color = Color.WHITE;
        }
        return color;
    }

    /**
     * Convert any int into the String equivalent
     * @param numTeam int to convert into a String
     * @return String corresponding to the given int
     */
    public static String numEquipeToString(int numTeam) {
        String team;
        switch (numTeam){
            case 1 -> team = "blue";
            case 2 -> team = "red";
            case 3 -> team = "green";
            case 4 -> team = "yellow";
            case 5 -> team = "purple";
            case 6 -> team = "pink";
            case 7 -> team = "orange";
            case 8 -> team = "cyan";
            default -> team = "neutre";
        }
        return team;
    }

    public static Image getCellSprite(Cell cell, int tailleCase) {
        Image sprite = null;
        if (cell instanceof Ground || cell instanceof SpawningCell) {
            switch (cell.getTeam()){
                case BLUE -> sprite = new Image("file:ressources/top/grounds/blue_ground.png", tailleCase, tailleCase, false, false);
                case RED -> sprite = new Image("file:ressources/top/grounds/red_ground.png", tailleCase, tailleCase, false, false);
                case GREEN -> sprite = new Image("file:ressources/top/grounds/green_ground.png", tailleCase, tailleCase, false, false);
                case YELLOW -> sprite = new Image("file:ressources/top/grounds/yellow_ground.png", tailleCase, tailleCase, false, false);
                case PURPLE -> sprite = new Image("file:ressources/top/grounds/purple_ground.png", tailleCase, tailleCase, false, false);
                case PINK -> sprite = new Image("file:ressources/top/grounds/pink_ground.png", tailleCase, tailleCase, false, false);
                case ORANGE -> sprite = new Image("file:ressources/top/grounds/orange_ground.png", tailleCase, tailleCase, false, false);
                case CYAN -> sprite = new Image("file:ressources/top/grounds/cyan_ground.png", tailleCase, tailleCase, false, false);
                default -> sprite = new Image("file:ressources/top/grounds/neutral_ground.png", tailleCase, tailleCase, false, false);
            }
        }
        else if (cell instanceof Wall) {
            sprite = new Image("file:ressources/top/wall.png", tailleCase, tailleCase, false, false);
        }
        return sprite;
    }

    public static Image getAgentSprite(Agent agent, int tailleAgent) {
        Image sprite;
        switch (agent.getTeam()){
            case BLUE -> sprite = new Image("file:ressources/top/robots/blue_robot.png", tailleAgent, tailleAgent, false, false);
            case RED -> sprite = new Image("file:ressources/top/robots/red_robot.png", tailleAgent, tailleAgent, false, false);
            case GREEN -> sprite = new Image("file:ressources/top/robots/green_robot.png", tailleAgent, tailleAgent, false, false);
            case YELLOW -> sprite = new Image("file:ressources/top/robots/yellow_robot.png", tailleAgent, tailleAgent, false, false);
            case PURPLE -> sprite = new Image("file:ressources/top/robots/purple_robot.png", tailleAgent, tailleAgent, false, false);
            case PINK -> sprite = new Image("file:ressources/top/robots/pink_robot.png", tailleAgent, tailleAgent, false, false);
            case ORANGE -> sprite = new Image("file:ressources/top/robots/orange_robot.png", tailleAgent, tailleAgent, false, false);
            case CYAN -> sprite = new Image("file:ressources/top/robots/cyan_robot.png", tailleAgent, tailleAgent, false, false);
            default -> sprite = new Image("file:ressources/placeholder.jpg", tailleAgent, tailleAgent, false, false);
        }
        return sprite;
    }
    public static Image getObjectSprite(GameObject object, int tailleObject) {
        Image sprite;
        if (object instanceof Flag) {
            Flag flag = (Flag) object;
            switch (flag.getTeam()) {
                case BLUE ->
                        sprite = new Image("file:ressources/top/flags/blue_flag.png", tailleObject, tailleObject, false, false);
                case RED ->
                        sprite = new Image("file:ressources/top/flags/red_flag.png", tailleObject, tailleObject, false, false);
                case GREEN ->
                        sprite = new Image("file:ressources/top/flags/green_flag.png", tailleObject, tailleObject, false, false);
                case YELLOW ->
                        sprite = new Image("file:ressources/top/flags/yellow_flag.png", tailleObject, tailleObject, false, false);
                case PURPLE ->
                        sprite = new Image("file:ressources/top/flags/purple_flag.png", tailleObject, tailleObject, false, false);
                case PINK ->
                        sprite = new Image("file:ressources/top/flags/pink_flag.png", tailleObject, tailleObject, false, false);
                case ORANGE ->
                        sprite = new Image("file:ressources/top/flags/orange_flag.png", tailleObject, tailleObject, false, false);
                case CYAN ->
                        sprite = new Image("file:ressources/top/flags/cyan_flag.png", tailleObject, tailleObject, false, false);
                default ->
                        sprite = new Image("file:ressources/placeholder.jpg", tailleObject, tailleObject, false, false);
            }
        }
        else {
            sprite = new Image("file:ressources/placeholder.jpg", tailleObject, tailleObject, false, false);
        }
        return sprite;
    }
}
