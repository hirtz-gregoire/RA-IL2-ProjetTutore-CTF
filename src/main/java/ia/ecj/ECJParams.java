package ia.ecj;

import engine.map.GameMap;
import ia.perception.Perception;
import ia.model.ModelEnum;

import java.io.Serializable;
import java.util.List;

public record ECJParams(int genomeSize, String mapPath, double playerSpeed, double rotateSpeed,int nbPlayer, int respawnTime, List<Integer> layers, List<Perception> perceptions, List<ModelEnum> modelsEnemy, List<String> modelsNNEnemy) implements Serializable {
}
