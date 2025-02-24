package ia.perception;

import engine.Team;
import engine.object.GameObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Filter {
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
}

