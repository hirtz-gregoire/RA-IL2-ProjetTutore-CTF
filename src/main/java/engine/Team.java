package engine;

public enum Team {
    NEUTRAL,
    BLUE,
    PINK;

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
    public static Team charToTeam(char teamNumber) {
        Team team;
        switch (teamNumber){
            case '1'-> team = Team.BLUE;
            case '2'-> team = Team.PINK;
            default -> team = Team.NEUTRAL;
        }
        return team;
    }
}
