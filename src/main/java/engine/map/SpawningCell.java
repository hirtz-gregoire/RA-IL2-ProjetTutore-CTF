package engine.map;

import engine.Team;

/**
 * Define a cell where any agent of the team can spawn
 */
public class SpawningCell extends Cell {
    /**
     * Team of this cell
     */
    private Team team;

    /**
     * Ground constructor, owned by the team given in param
     * @param team The team owning this ground cell
     */
    public SpawningCell(Team team) {
        super.isWalkable = true;
        super.team = team;
    }
/**
     * @return The team of this cell
     */
    public Team getTeam() {
        return team;
}
