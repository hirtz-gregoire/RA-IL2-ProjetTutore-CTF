package ia.ecj;

import ia.perception.Perception;
import ia.model.ModelEnum;

import java.io.Serializable;
import java.util.List;

public record ECJParams(int genomeSize, String mapPath, double playerSpeed, double rotateSpeed, int nbPlayer,
                        int respawnTime, int maxTurns, List<Integer> layers, List<Perception> perceptions, List<ModelEnum> modelsTeams,
                        List<String> modelsNNTeams, ia.model.NeuralNetworks.MLP.TransferFunction transferFunction)
        implements Serializable {
}
