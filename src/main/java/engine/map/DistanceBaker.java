package engine.map;

import engine.Team;
import engine.Vector2;
import engine.object.Flag;
import ia.perception.PerceptionRaycast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.function.BiConsumer;

public class DistanceBaker {
    /**
     * Datastruct used to store distance information about a cell. We don't store the cell to avoid recursive hell
     */
    public static class BakingCell implements Comparable<BakingCell> {
        public double distance;
        public int x, y;
        public int parentX, parentY;

        public BakingCell(double distance, int x, int y, int parentX, int parentY) {
            this.x = x;
            this.y = y;
            this.parentX = parentX;
            this.parentY = parentY;
            this.distance = distance;
        }

        @Override
        public int compareTo(BakingCell o) {
            return Double.compare(this.distance, o.distance);
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    /**
     * Update the distance to each flag from the cells of the map. No output : the data is directly inserted into cell objects
     * @param flags The list of flag for which we want to update the distance map
     * @param map The map where we're going to compute the distances
     */
    public static void computeDistancesForFlags(List<Flag> flags, GameMap map) {
        for (Flag flag : flags) {
            // Note :
            // I'm lazy, I don't take into account the fact that flags might not be aligned to the grid
            var coordinate = flag.getCoordinate();
            var bakingCell = new BakingCell(0, (int) coordinate.x(), (int) coordinate.y(), (int) coordinate.x(), (int) coordinate.y());
            // Fancy callback to set the data so we don't have to pass a whole map around
            computeDistances(List.of(bakingCell), map, ((bc, cell) -> cell.setBakedFlagData(flag, bc)));
        }
    }

    /**
     * Update the distance to the given territory from the cells of the map. No output : the data is directly inserted into cell objects
     * @param startingCells Starting cell of the algorithm, from where all distances will be computed : you have to give all cells of the territory yourself (which is normally faster)
     * @param map The map where we're going to compute the distances
     * @param territoryTeam To which team do we want to push the distances to
     */
    public static void computeDistancesForTerritoryCell(List<Cell> startingCells, GameMap map, Team territoryTeam) {
        List<BakingCell> bakingCells = new ArrayList<>();
        for (Cell cell : startingCells) {
            var coordinate = cell.getCoordinate();
            bakingCells.add(new BakingCell(0, (int) coordinate.x(), (int) coordinate.y(), (int) coordinate.x(), (int) coordinate.y()));
        }

        // Fancy callback to set the data so we don't have to pass a whole map around
        computeDistances(bakingCells, map, (bakingCell, cell) -> cell.setBakedTerritoryData(territoryTeam, bakingCell));
    }

    /**
     * Runs dijkstra starting from the given cells to compute the distance to all the other cells on the map. The output callback is used to export data in a fast way (instead of exporting the whole data structure)
     * @param startingCells The cells from where the paths start
     * @param map The map we want to bake the distance on
     * @param output The output callback used to update the real cells
     */
    private static void computeDistances(List<BakingCell> startingCells, GameMap map, BiConsumer<BakingCell, Cell> output) {
        // I wish I didn't had to create this array, but it makes the algorithm simpler to look at
        BakingCell[][] bakingCells = new BakingCell[map.getWidth()][map.getHeight()];
        for(int x = 0; x < map.getWidth(); x++) {
            for(int y = 0; y < map.getHeight(); y++) {
                var cell = map.getCellFromXY(x, y);
                var coordinate = cell.getCoordinate();
                bakingCells[x][y] = new BakingCell(1000, (int) coordinate.x(), (int) coordinate.y(), (int) coordinate.x(), (int) coordinate.y());
            }
        }

        // Don't forget to put the cells we were given into the algorithm
        // They are supposed to be initialized with base distances
        PriorityQueue<BakingCell> priorityQueue = new PriorityQueue<>();
        for(BakingCell bakingCell : startingCells) {
            bakingCells[bakingCell.x][bakingCell.y] = bakingCell;
            priorityQueue.add(bakingCell);
        }

        // Be prepared to a lot of reference juggling :
        // Our bakingCells array contain data for the algorithm
        // While map.cells contains data about the map
        while(!priorityQueue.isEmpty()) {
            var bakingCell = priorityQueue.poll();
            var bakingParent = bakingCells[bakingCell.parentX][bakingCell.parentY];

            // An example of why we still needs the original cell values : it contains pre-computed neighbours
            for(Cell neighbor : map.getCellFromXY(bakingCell.x, bakingCell.y).getNeighbours()) {

                // Stay sharp : there is two type of neighbors : algorithm data and map data
                var neighborCoordinate = neighbor.getCoordinate();
                BakingCell bakingNeighbor = bakingCells[(int)neighborCoordinate.x()][(int)neighborCoordinate.y()];

                // The search is kind of breadth-first, so it's almost safe to do this
                if(bakingNeighbor.distance <= bakingCell.distance) continue;

                var newDistance = bakingCell.distance + neighborCoordinate.distance(bakingCell.x, bakingCell.y);
                var newParentX = bakingCell.x;
                var newParentY = bakingCell.y;

                // Theta* path optimization : if a direct path between the neighbour and our parent exist, we connect them
                if(checkLOS(bakingParent, bakingNeighbor, map)) {
                    var shortcutDistance = bakingParent.distance + neighborCoordinate.distance(bakingParent.x, bakingParent.y);

                    if(shortcutDistance < newDistance) {
                        newDistance = shortcutDistance;
                        newParentX = bakingParent.x;
                        newParentY = bakingParent.y;
                    }
                }

                // Final data update
                if(newDistance < bakingNeighbor.distance) {
                    bakingNeighbor.distance = newDistance;
                    bakingNeighbor.parentX = newParentX;
                    bakingNeighbor.parentY = newParentY;
                    if(!neighbor.isWalkable()) continue; // We ignore walls
                    priorityQueue.add(bakingNeighbor);
                }
            }
        }

        // Push the new data
        for(int x = 0; x < map.getWidth(); x++) {
            for(int y = 0; y < map.getHeight(); y++) {
                output.accept(bakingCells[x][y], map.getCellFromXY(x, y));
            }
        }
    }

    /**
     * Check if a straight line exist between the two given cells
     * @param start The cell from where the LOS check will start
     * @param end The cell to which the LOS check will go
     * @param map The map where to perform the LOS check
     * @return Return true if a straight line exist between the two given cells, return false otherwise
     */
    private static boolean checkLOS(BakingCell start, BakingCell end, GameMap map) {
        var startVector = new Vector2(start.x + 0.5, start.y + 0.5);
        var rayVector = new Vector2(end.x - start.x, end.y - start.y);
        return PerceptionRaycast.wallCast(startVector, rayVector.getAngle(), rayVector.length(), map) == null;
    }

    /**
     * Get the Bilinear interpolation of distances of the 4 surrounding cells of the given position
     * @param coordinate The position to start the interpolation
     * @param map The map where we want to fetch the distances from
     * @param territoryTeam The territory we want to fetch the distances from
     * @return Return the interpolated distance
     */
    public static double computeDistance(Vector2 coordinate, GameMap map, Team territoryTeam) {
        int x = (int) (coordinate.x() - 0.5);
        int y = (int) (coordinate.y() - 0.5);

        // Fetch cells, ensuring they are not null
        Cell cell00 = map.getCellFromXY(x, y);
        Cell cell10 = map.getCellFromXY(x + 1, y);
        Cell cell01 = map.getCellFromXY(x, y + 1);
        Cell cell11 = map.getCellFromXY(x + 1, y + 1);

        // Fetch coordinates & values if the cell exists
        Double val00 = (cell00 != null) ? cell00.getBakedTerritoryData(territoryTeam).distance : null;
        Double val10 = (cell10 != null) ? cell10.getBakedTerritoryData(territoryTeam).distance : null;
        Double val01 = (cell01 != null) ? cell01.getBakedTerritoryData(territoryTeam).distance : null;
        Double val11 = (cell11 != null) ? cell11.getBakedTerritoryData(territoryTeam).distance : null;

        return computeDistance(coordinate, cell00, cell10, cell01, cell11, val00, val10, val01, val11);
    }

    /**
     * Get the Bilinear interpolation of distances of the 4 surrounding cells of the given position
     * @param coordinate The position to start the interpolation
     * @param map The map where we want to fetch the distances from
     * @param flag The flag we want to fetch the distances from
     * @return Return the interpolated distance
     */
    public static double computeDistance(Vector2 coordinate, GameMap map, Flag flag) {
        int x = (int) (coordinate.x() - 0.5);
        int y = (int) (coordinate.y() - 0.5);

        // Fetch cells, ensuring they are not null
        Cell cell00 = map.getCellFromXY(x, y);
        Cell cell10 = map.getCellFromXY(x + 1, y);
        Cell cell01 = map.getCellFromXY(x, y + 1);
        Cell cell11 = map.getCellFromXY(x + 1, y + 1);

        // Fetch coordinates & values if the cell exists
        Double val00 = (cell00 != null) ? cell00.getBakedFlagData(flag).distance : null;
        Double val10 = (cell10 != null) ? cell10.getBakedFlagData(flag).distance : null;
        Double val01 = (cell01 != null) ? cell01.getBakedFlagData(flag).distance : null;
        Double val11 = (cell11 != null) ? cell11.getBakedFlagData(flag).distance : null;

        return computeDistance(coordinate, cell00, cell10, cell01, cell11, val00, val10, val01, val11);
    }

    private static double computeDistance(Vector2 coordinate, Cell cell00, Cell cell10, Cell cell01, Cell cell11, Double val00, Double val10, Double val01, Double val11) {
        // Handle cases with missing values
        if (val00 == null && val10 == null && val01 == null && val11 == null) {
            return Double.NaN; // No data available
        }

        if (val00 != null && val10 == null && val01 == null && val11 == null) {
            return val00; // Only bottom-left available
        }
        if (val10 != null && val00 == null && val01 == null && val11 == null) {
            return val10; // Only bottom-right available
        }
        if (val01 != null && val00 == null && val10 == null && val11 == null) {
            return val01; // Only top-left available
        }
        if (val11 != null && val00 == null && val10 == null && val01 == null) {
            return val11; // Only top-right available
        }

        // Interpolation logic (adapted for missing values)
        double wX = 0.5, wY = 0.5; // Default values
        double distX0 = 0, distX1 = 0;

        if (val00 != null && val10 != null) {
            wX = (coordinate.x() - cell00.getCoordinate().x()) / (cell10.getCoordinate().x() - cell00.getCoordinate().x() + 0.00000001) ;
            distX0 = (1 - wX) * val00 + wX * val10;
        } else if (val00 != null) {
            distX0 = val00;
        } else if (val10 != null) {
            distX0 = val10;
        }

        if (val01 != null && val11 != null) {
            wX = (coordinate.x() - cell01.getCoordinate().x()) / (cell11.getCoordinate().x() - cell01.getCoordinate().x() + 0.00000001);
            distX1 = (1 - wX) * val01 + wX * val11;
        } else if (val01 != null) {
            distX1 = val01;
        } else if (val11 != null) {
            distX1 = val11;
        }

        if (val00 != null && val01 != null) {
            wY = (coordinate.y() - cell00.getCoordinate().y()) / (cell01.getCoordinate().y() - cell00.getCoordinate().y() + 0.00000001);
        } else if (val10 != null && val11 != null) {
            wY = (coordinate.y() - cell10.getCoordinate().y()) / (cell11.getCoordinate().y() - cell10.getCoordinate().y() + 0.00000001);
        }

        if(Double.isNaN(wX)) System.err.println("wX is Nan");
        if(Double.isNaN(wY)) System.err.println("wY is NaN");
        if(Double.isNaN(distX0)) System.err.println("distX0 is NaN");
        if(Double.isNaN(distX1)) System.err.println("distX1 is NaN");
        return (1 - wY) * distX0 + wY * distX1;
    }

}
