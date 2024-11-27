package engine;

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
}
