package ia.ecj;

import ia.perception.Perception;
import ia.model.ModelEnum;

import java.io.Serializable;
import java.util.List;

public record ECJParams(int genomeSize, List<String> mapPath, double playerSpeed, double rotateSpeed, int nbPlayer, int respawnTime, List<Integer> layers, List<Perception> perceptions, List<List<ModelEnum>> modelsTeams, List<List<String>> modelsNNTeams,
                        ia.model.NeuralNetworks.MLP.TransferFunction transferFunction, int memorySize, String mlpFile,
                        int maxTurns) implements Serializable {
}
