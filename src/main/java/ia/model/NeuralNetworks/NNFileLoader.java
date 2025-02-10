package ia.model.NeuralNetworks;

import engine.Team;
import engine.agent.Agent;
import ia.model.NeuralNetworks.MLP.Hyperbolic;
import ia.model.NeuralNetworks.MLP.MLP;
import ia.model.NeuralNetworks.MLP.Sigmoid;
import ia.model.NeuralNetworks.MLP.TransferFunction;
import ia.perception.*;

import javax.naming.OperationNotSupportedException;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class NNFileLoader {
    /**
     * A USABLE NETWORK FOR THE CTF PROJECT IS DEFINED AS FOLLOW :
     *
     * fileName : name.CTF
     * content (don't forget line breaks) :
     *
     * PerceptionName;param1;param2;param3 <- the different perceptions used by the model, params depend on the perceptions
     * PerceptionName;param1;param2;param3;param4
     * PerceptionName;param1;param2
     * [etc]
     *
     * anyName.anyExtension <- this is the file containing the weights of the network (depend on the type of network, detected via the file extension)
     */

    /**
     * Map each class to their respective constructor
     */
    private static final Map<Class<?>, Function<String[], Perception>> CLASS_MAP = Map.of(
            NearestAgentCompass.class, (String[] tokens) -> new NearestAgentCompass(null, Team.valueOf(tokens[1])),
            NearestAllyFlagCompass.class, (String[] tokens) -> new NearestAllyFlagCompass(null, Team.valueOf(tokens[1]), Boolean.parseBoolean(tokens[2])),
            NearestEnemyFlagCompass.class, (String[] tokens) -> new NearestEnemyFlagCompass(null, Team.valueOf(tokens[1]), Boolean.parseBoolean(tokens[2])),
            PerceptionRaycast.class, (String[] tokens) -> tokens[1].contains(" ")
                    ? new PerceptionRaycast(null, Arrays.stream(tokens[1].split(" ")).mapToDouble(Double::parseDouble).toArray(), Integer.parseInt(tokens[2]), Double.parseDouble(tokens[3]))
                    : new PerceptionRaycast(null, Double.parseDouble(tokens[1]), Integer.parseInt(tokens[2]), Double.parseDouble(tokens[3])),
            TerritoryCompass.class, (String[] tokens) -> new TerritoryCompass(null, Team.valueOf(tokens[1]))
    );

    private static final int STATE_PERCEPTION = 0;
    private static final int STATE_FILE = 1;

    public static ModelNeuralNetwork loadModel(String fileName) throws IOException {
        File file = new File(fileName);
        FileReader reader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(reader);

        List<Perception> perceptions = new ArrayList<Perception>();

        int state = STATE_PERCEPTION;
        String line = bufferedReader.readLine().trim();
        while (line != null) {
            if(line.isBlank()) {
                state = STATE_FILE;
                line = bufferedReader.readLine().trim();
                continue;
            }
            String[] tokens = line.split(";");

            if(state == STATE_PERCEPTION) {
                try {
                    Class<?> clazz = Class.forName(tokens[0]);

                    Perception perception = CLASS_MAP.getOrDefault(clazz, (String[] _) -> {
                        throw new IllegalArgumentException("Unknown perception: " + tokens[0]);
                    }).apply(tokens);

                    if(perception != null) perceptions.add(perception);
                } catch (ClassNotFoundException e) {
                    throw new IllegalArgumentException("Unknown perception: " + tokens[0]);
                }
            }
            else {
                var nn = switch (Arrays.asList(tokens[0].split("\\.")).getLast()) {
                    case "mlp" -> loadMLPNetwork(tokens[0]);
                    case "dl4j" -> loadDL4JNetwork(tokens[0]);
                    default -> throw new IllegalArgumentException("Unknown extension: " + tokens[0]);
                };

                return new ModelNeuralNetwork(nn, perceptions);
            }
            line = bufferedReader.readLine().trim();
        }

        return null;
    }

    /**
     * A USABLE NETWORK FOR THE CTF PROJECT IS DEFINED AS FOLLOW :
     * fileName : name.mlp
     * content (don't forget line breaks) :
     *
     * transfertFuntion <- transfertFunction = TransfertFunction (ex : Sigmoid, Hyperbolic)
     * numberOfNeuronsLayer1;numberOfNeuronsLayer2;[etc] <- numberOfNeurons = int (ex : 10)
     * weight1;weight2;[etc] <- weight = double (ex : 0.1234)
     */
    private static NeuralNetwork loadMLPNetwork(String filename) throws IOException {
        File file = new File(filename);
        FileReader reader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(reader);

        //Transfert Function
        String line = bufferedReader.readLine().trim();
        TransferFunction transferFunction;
        switch (line) {
            case "Sigmoid" -> transferFunction = new Sigmoid();
            case "Hyperbolic" -> transferFunction = new Hyperbolic();
            default -> throw new IllegalArgumentException("Unknown transferFunction: " + line);
        }

        //Layers
        line = bufferedReader.readLine().trim();
        String[] tokens = line.split(";");
        int[] layers = new int[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            layers[i] = Integer.parseInt(tokens[i]);
        }

        //Weights
        line = bufferedReader.readLine().trim();
        tokens = line.split(";");
        double[] weights = new double[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            System.out.println(tokens[i]);
            weights[i] = Double.parseDouble(tokens[i]);
        }

        MLP mlp = new MLP(layers, transferFunction);
        mlp.insertWeights(weights);
       return mlp;
    }

    private static NeuralNetwork loadDL4JNetwork(String filename) {
        throw new UnsupportedOperationException();
    }
}
