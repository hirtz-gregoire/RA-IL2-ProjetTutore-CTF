package engine;

import javafx.scene.image.Image;

public enum Team {
    NEUTRAL,
    BLUE,
    PINK;

    /**
     * Convert any Team instance into the char equivalent
     * @param team Enum value to convert into char
     * @return Char representing enum value
     */
    public static char teamToChar(Team team) {
        char teamChar;
        switch (team){
            case Team.BLUE -> teamChar = '1';
            case Team.PINK -> teamChar = '2';
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
            case '2'-> team = Team.PINK;
            default -> team = Team.NEUTRAL;
        }
        return team;
    }

    public static Image getGroundSprite(Team team) {
        Image sprite;
        switch (team){
            case BLUE -> sprite = new Image("sol_bleu.png");
            case PINK -> sprite = new Image("sol_rouge.png");
            default -> sprite = new Image("sol_neutre.png");
        }
        return sprite;
    }

    public static Image getAgentSprite(Team team) {
        Image sprite;
        switch (team){
            case BLUE -> sprite = new Image("Bleu/robot_bleu_flat_haut.png");
            case PINK -> sprite = new Image("Rouge/robot_rouge_flat_haut.png");
            default -> sprite = new Image("placeholder.jpg");
        }
        return sprite;
    }

}
