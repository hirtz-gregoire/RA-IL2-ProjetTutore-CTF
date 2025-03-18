package engine.map;

import engine.Vector2;
import engine.Team;
import engine.object.Flag;
import engine.object.GameObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Cell, or tile of the map
 */
public abstract class Cell extends GameObject {

    /** Define whether the tile is walkable */
    protected boolean isWalkable;

    // Don't forget to re-compute these values if the map or the flag position changes
    protected final Map<Flag, DistanceBaker.BakingCell> bakedFlagDistances = new HashMap<>();
    protected final Map<Team, DistanceBaker.BakingCell> bakedTerritoryDistances = new HashMap<>();
    protected Cell[] neighbours;

    protected Cell(Vector2 coordinate, Team team) {
        super(coordinate, team);
    }

    public boolean isWalkable() {
        return isWalkable;
    }

    public abstract Cell copy();

    public void setBakedFlagData(Flag flag, DistanceBaker.BakingCell pathData) {
        bakedFlagDistances.put(flag, pathData);
    }

    public DistanceBaker.BakingCell getBakedFlagData(Flag flag) {
        return bakedFlagDistances.get(flag);
    }

    public void setBakedTerritoryData(Team team, DistanceBaker.BakingCell pathData) {
        bakedTerritoryDistances.put(team, pathData);
    }

    public DistanceBaker.BakingCell getBakedTerritoryData(Team team) {
        return bakedTerritoryDistances.get(team);
    }

    public void setNeighbours(Cell[] neighbours) {
        this.neighbours = neighbours;
    }

    public Cell[] getNeighbours() {
        return neighbours;
    }

    public void clearBakingData() {
        bakedFlagDistances.clear();
        bakedTerritoryDistances.clear();
    }
}
