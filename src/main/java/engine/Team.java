package engine;

import engine.agent.Agent;
import engine.map.Cell;
import engine.map.Ground;
import engine.map.SpawningCell;
import engine.map.Wall;
import javafx.scene.image.Image;

public enum Team {
    NEUTRAL,
    BLUE,
    RED;

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
            case Team.NEUTRAL -> teamChar = '0';
            default -> teamChar = '-';
        }
        return teamChar;
    }

    /**
     * Convert any char into the char equivalent
     * @param teamChar Char to convert into a Team
     * @return Team corresponding to the given char
     */
    public static Team charToTeam(char teamChar) {
        Team team;
        switch (teamChar){
            case '1'-> team = Team.BLUE;
            case '2'-> team = Team.RED;
            default -> team = Team.NEUTRAL;
        }
        return team;
    }

    public static Image getCellSprite(Cell cell, int tailleCase) {
        Image sprite = null;
        if (cell instanceof Ground || cell instanceof SpawningCell) {
            switch (cell.getTeam()){
                case BLUE -> sprite = new Image("file:ressources/top/sol_bleu.png", tailleCase, tailleCase, false, false);
                case RED -> sprite = new Image("file:ressources/top/sol_rouge.png", tailleCase, tailleCase, false, false);
                default -> sprite = new Image("file:ressources/top/sol_neutre.png", tailleCase, tailleCase, false, false);
            }
        }
        else if (cell instanceof Wall) {
            sprite = new Image("file:ressources/top/mur_vue_haut.png", tailleCase, tailleCase, false, false);
        }
        return sprite;
    }

    public static Image getAgentSprite(Agent agent, int taille) {
        Image sprite;
        switch (agent.getTeam()){
            case BLUE -> sprite = new Image("file:ressources/top/robot/Bleu/robot_bleu_flat_haut.png", taille, taille, false, false);
            case RED -> sprite = new Image("file:ressources/top/robot/Rouge/robot_rouge_flat_haut.png", taille, taille, false, false);
            default -> sprite = new Image("file:ressources/placeholder.jpg", taille, taille, false, false);
        }
        return sprite;
    }
}
