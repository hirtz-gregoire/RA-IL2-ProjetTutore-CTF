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

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

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
            case 1 -> team = "bleu";
            case 2 -> team = "rouge";
            case 3 -> team = "vert";
            case 4 -> team = "jaune";
            case 5 -> team = "violet";
            case 6 -> team = "rose";
            case 7 -> team = "orange";
            case 8 -> team = "cyan";
            default -> team = "neutre";
        }
        return team;
    }

    public static Image getCellSprite(Cell cell, double cellSize) {
        Image sprite = null;
        if (cell instanceof Ground || cell instanceof SpawningCell) {
            switch (cell.getTeam()){
                case BLUE -> sprite = new Image("file:ressources/top/grounds/blue_ground.png", cellSize, cellSize, false, false);
                case RED -> sprite = new Image("file:ressources/top/grounds/red_ground.png", cellSize, cellSize, false, false);
                case GREEN -> sprite = new Image("file:ressources/top/grounds/green_ground.png", cellSize, cellSize, false, false);
                case YELLOW -> sprite = new Image("file:ressources/top/grounds/yellow_ground.png", cellSize, cellSize, false, false);
                case PURPLE -> sprite = new Image("file:ressources/top/grounds/purple_ground.png", cellSize, cellSize, false, false);
                case PINK -> sprite = new Image("file:ressources/top/grounds/pink_ground.png", cellSize, cellSize, false, false);
                case ORANGE -> sprite = new Image("file:ressources/top/grounds/orange_ground.png", cellSize, cellSize, false, false);
                case CYAN -> sprite = new Image("file:ressources/top/grounds/cyan_ground.png", cellSize, cellSize, false, false);
                default -> sprite = new Image("file:ressources/top/grounds/neutral_ground.png", cellSize, cellSize, false, false);
            }
        }
        else if (cell instanceof Wall) {
            sprite = new Image("file:ressources/top/wall.png", cellSize, cellSize, false, false);
        }
        return sprite;
    }

    private record TeamSizePair(Team team, double size) {}
    private static final Map<TeamSizePair, Image> agentSpriteBuffer = new HashMap<TeamSizePair, Image>();
    public static Image getAgentSprite(Agent agent, double tailleAgent) {
        return switch (agent.getTeam()){
            case BLUE -> agentSpriteBuffer.computeIfAbsent(new TeamSizePair(BLUE, tailleAgent), _ -> new Image("file:ressources/top/robots/blue_robot.png", tailleAgent, tailleAgent, false, false));
            case RED -> agentSpriteBuffer.computeIfAbsent(new TeamSizePair(RED, tailleAgent), _ -> new Image("file:ressources/top/robots/red_robot.png", tailleAgent, tailleAgent, false, false));
            case GREEN -> agentSpriteBuffer.computeIfAbsent(new TeamSizePair(GREEN, tailleAgent) , _ -> new Image("file:ressources/top/robots/green_robot.png", tailleAgent, tailleAgent, false, false));
            case YELLOW -> agentSpriteBuffer.computeIfAbsent(new TeamSizePair(YELLOW, tailleAgent), _ -> new Image("file:ressources/top/robots/yellow_robot.png", tailleAgent, tailleAgent, false, false));
            case PURPLE -> agentSpriteBuffer.computeIfAbsent(new TeamSizePair(PURPLE, tailleAgent), _ -> new Image("file:ressources/top/robots/purple_robot.png", tailleAgent, tailleAgent, false, false));
            case PINK -> agentSpriteBuffer.computeIfAbsent(new TeamSizePair(PINK, tailleAgent), _ -> new Image("file:ressources/top/robots/pink_robot.png", tailleAgent, tailleAgent, false, false));
            case ORANGE -> agentSpriteBuffer.computeIfAbsent(new TeamSizePair(ORANGE, tailleAgent), _ -> new Image("file:ressources/top/robots/orange_robot.png", tailleAgent, tailleAgent, false, false));
            case CYAN -> agentSpriteBuffer.computeIfAbsent(new TeamSizePair(CYAN, tailleAgent), _ -> new Image("file:ressources/top/robots/cyan_robot.png", tailleAgent, tailleAgent, false, false));
            default -> new Image("file:ressources/placeholder.jpg", tailleAgent, tailleAgent, false, false);
        };
    }
    private static final Map<TeamSizePair, Image> flagSpriteBuffer = new HashMap<TeamSizePair, Image>();
    public static Image getObjectSprite(GameObject object, double objectSize) {
        if (object instanceof Flag flag) {
            return switch (flag.getTeam()) {
                case BLUE -> flagSpriteBuffer.computeIfAbsent(new TeamSizePair(BLUE, objectSize), _ -> new Image("file:ressources/top/flags/blue_flag.png", objectSize, objectSize, false, false));
                case RED -> flagSpriteBuffer.computeIfAbsent(new TeamSizePair(RED, objectSize), _ -> new Image("file:ressources/top/flags/red_flag.png", objectSize, objectSize, false, false));
                case GREEN -> flagSpriteBuffer.computeIfAbsent(new TeamSizePair(GREEN, objectSize), _ -> new Image("file:ressources/top/flags/green_flag.png", objectSize, objectSize, false, false));
                case YELLOW -> flagSpriteBuffer.computeIfAbsent(new TeamSizePair(YELLOW, objectSize), _ -> new Image("file:ressources/top/flags/yellow_flag.png", objectSize, objectSize, false, false));
                case PURPLE ->flagSpriteBuffer.computeIfAbsent(new TeamSizePair(PURPLE, objectSize), _ -> new Image("file:ressources/top/flags/purple_flag.png", objectSize, objectSize, false, false));
                case PINK ->flagSpriteBuffer.computeIfAbsent(new TeamSizePair(PINK, objectSize), _ -> new Image("file:ressources/top/flags/pink_flag.png", objectSize, objectSize, false, false));
                case ORANGE ->flagSpriteBuffer.computeIfAbsent(new TeamSizePair(ORANGE, objectSize), _ -> new Image("file:ressources/top/flags/orange_flag.png", objectSize, objectSize, false, false));
                case CYAN ->flagSpriteBuffer.computeIfAbsent(new TeamSizePair(CYAN, objectSize), _ -> new Image("file:ressources/top/flags/cyan_flag.png", objectSize, objectSize, false, false));
                default -> new Image("file:ressources/placeholder.jpg", objectSize, objectSize, false, false);
            };
        }
        else {
            return new Image("file:ressources/placeholder.jpg", objectSize, objectSize, false, false);
        }
    }
}
