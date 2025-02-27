package ia.perception;

import engine.Team;
import engine.map.Cell;
import engine.object.GameObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Filter implements Serializable {
    public TeamMode getTeamMode() {
        return teamMode;
    }

    /**
     * finding nearest cell of a specific team
     * @param cells list of all cells of the map
     * @return nearest agents from our agent
     */
    public Cell nearestCell(GameObject object, List<List<Cell>> cells) {
        int rows = cells.size();
        int cols = cells.getFirst().size();
        int centerX = (int)Math.floor(object.getCoordinate().x());
        int centerY = (int)Math.floor(object.getCoordinate().y());
        int maxRadius = Math.max(rows, cols);

        for (int r = 0; r < maxRadius; r++) {
            Cell closestCell = null;
            double closestDistance = Double.MAX_VALUE;

            for (int i = -r; i <= r; i++) {
                int x, y;

                // Top row
                x = centerX - r;
                y = centerY + i;
                if (isPositionValid(x, y, rows, cols)) {
                    Cell cell = cells.get(x).get(y);
                    if(
                            isValidCell(object, cell)
                    ) {
                        var coord = cell.getCoordinate();
                        var otherCoord = object.getCoordinate();
                        double xDist = coord.x() + 0.5 - otherCoord.x();
                        double yDist = coord.y() + 0.5 - otherCoord.y();
                        double dist = Math.sqrt(Math.pow(xDist, 2) + Math.pow(yDist, 2));
                        if (dist < closestDistance) {
                            closestCell = cell;
                            closestDistance = dist;
                        }
                    }
                }

                // Bottom row
                x = centerX + r;
                y = centerY + i;
                if (isPositionValid(x, y, rows, cols)) {
                    Cell cell = cells.get(x).get(y);
                    if(isValidCell(object, cell)) {
                        var coord = cell.getCoordinate();
                        var otherCoord = object.getCoordinate();
                        double xDist = coord.x() + 0.5 - otherCoord.x();
                        double yDist = coord.y() + 0.5 - otherCoord.y();
                        double dist = Math.sqrt(Math.pow(xDist, 2) + Math.pow(yDist, 2));
                        if (dist < closestDistance) {
                            closestCell = cell;
                            closestDistance = dist;
                        }
                    }
                }

                // Left column (skip corners)
                x = centerX + i;
                y = centerY - r;
                if (isPositionValid(x, y, rows, cols) && i != -r && i != r) {
                    Cell cell = cells.get(x).get(y);
                    if(isValidCell(object, cell)) {
                        var coord = cell.getCoordinate();
                        var otherCoord = object.getCoordinate();
                        double xDist = coord.x() + 0.5 - otherCoord.x();
                        double yDist = coord.y() + 0.5 - otherCoord.y();
                        double dist = Math.sqrt(Math.pow(xDist, 2) + Math.pow(yDist, 2));
                        if (dist < closestDistance) {
                            closestCell = cell;
                            closestDistance = dist;
                        }
                    }
                }

                // Right column (skip corners)
                x = centerX + i;
                y = centerY + r;
                if (isPositionValid(x, y, rows, cols) && i != -r && i != r) {
                    Cell cell = cells.get(x).get(y);
                    if(isValidCell(object, cell)) {
                        var coord = cell.getCoordinate();
                        var otherCoord = object.getCoordinate();
                        double xDist = coord.x() + 0.5 - otherCoord.x();
                        double yDist = coord.y() + 0.5 - otherCoord.y();
                        double dist = Math.sqrt(Math.pow(xDist, 2) + Math.pow(yDist, 2));
                        if (dist < closestDistance) {
                            closestCell = cell;
                            closestDistance = dist;
                        }
                    }
                }
            }

            if(closestCell != null) {
                return closestCell;
            }
        }
        return null;
    }

    private boolean isValidCell(GameObject object, Cell cell) {
        return switch (teamMode) {
            case ANY -> true;
            case ALLY -> cell.getTeam() == object.getTeam();
            case ENEMY -> cell.getTeam() != object.getTeam() && cell.getTeam() != Team.NEUTRAL;
            case NEUTRAL -> cell.getTeam() == Team.NEUTRAL;
            case null, default -> false;
        };
    }

    private static boolean isPositionValid(int x, int y, int rows, int cols) {
        return x >= 0 && x < rows && y >= 0 && y < cols;
    }

    public enum TeamMode {
        ALLY,
        ENEMY,
        NEUTRAL,
        ANY
    }

    public enum DistanceMode {
        NEAREST,
        FARTHEST
    }

    private TeamMode teamMode;
    private DistanceMode distanceMode;

    public Filter(TeamMode teamMode, DistanceMode distanceMode) {
        this.teamMode = teamMode;
        this.distanceMode = distanceMode;
    }

    public <T extends GameObject> List<T> filterByTeam(Team team, List<? extends GameObject> objects, Class<T> type) {
        var filter = getTeamFilter(team);

        List<GameObject> filteredList = filterByClass(objects, type);
        return (List<T>) filter.apply(filteredList);
    }

    public <T extends GameObject> List<GameObject> filterByClass(List<? extends GameObject> objects, Class<T> type) {
        List<GameObject> filteredList = new ArrayList<>();
        for (GameObject o : objects) {
            if (type.isInstance(o)) {
                filteredList.add(o);
            }
        }
        return filteredList;
    }

    public <T extends GameObject> List<T> filterByDistance(List<? extends GameObject> objects, GameObject target, Class<T> type) {
        var filter = getDistanceFilter(target);

        List<GameObject> filteredList = filterByClass(objects, type);
        return (List<T>) filter.apply(filteredList);
    }

    public <T extends GameObject> List<T> customFilter(List<? extends GameObject> objects, Class<T> type, Function<GameObject, T> filter) {
        List<GameObject> filteredList = new ArrayList<>();
        for (GameObject o : filterByClass(objects, type)) {
            if (type.isInstance(o)) {
                T object = filter.apply(o);
                if(object != null) {
                    filteredList.add(object);
                }
            }
        }
        return (List<T>) filteredList;
    }

    private Function<List<GameObject>, List<GameObject>> getTeamFilter(Team team){

        return switch (teamMode) {
            case ALLY -> objects -> {
                List<GameObject> list = new ArrayList<>();
                for (GameObject o : objects) {
                    if (o.getTeam().equals(team)) {
                        list.add(o);
                    }
                }
                return list;
            };
            case ENEMY -> objects -> {
                List<GameObject> list = new ArrayList<>();
                for (GameObject o : objects) {
                    if (!o.getTeam().equals(team)) {
                        list.add(o);
                    }
                }
                return list;
            };
            case NEUTRAL -> objects -> {
                List<GameObject> list = new ArrayList<>();
                for (GameObject o : objects) {
                    if (o.getTeam().equals(Team.NEUTRAL)) {
                        list.add(o);
                    }
                }
                return list;
            };
            default -> objects -> objects;
        };

    }

    private Function<List<GameObject>, List<GameObject>> getDistanceFilter(GameObject currentObject){
        return switch (distanceMode) {
            case NEAREST -> objects -> {
                objects.sort((o1, o2) -> {
                    double distance1 = currentObject.getCoordinate().distance(o1.getCoordinate());
                    double distance2 = currentObject.getCoordinate().distance(o2.getCoordinate());
                    return Double.compare(distance1, distance2);
                });
                return objects;
            };
            case null, default -> objects -> {
                objects.sort((o1, o2) -> {
                    double distance1 = currentObject.getCoordinate().distance(o1.getCoordinate());
                    double distance2 = currentObject.getCoordinate().distance(o2.getCoordinate());
                    return Double.compare(distance1, distance2);
                });
                return objects.reversed();
            };
        };
    }

    public void setTeamMode(TeamMode teamMode) {
        this.teamMode = teamMode;
    }

    public void setDistanceMode(DistanceMode distanceMode) {
        this.distanceMode = distanceMode;
    }
}

