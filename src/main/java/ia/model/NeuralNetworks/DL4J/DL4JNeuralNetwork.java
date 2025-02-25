package ia.model.NeuralNetworks.DL4J;
import ia.model.NeuralNetworks.NeuralNetwork;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.deeplearning4j.nn.params.DefaultParamInitializer;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.lossfunctions.LossFunctions;

public class DL4JNeuralNetwork implements NeuralNetwork {

    private MultiLayerNetwork network;

    public DL4JNeuralNetwork(int[] layers) {
        // Setup global parameters
        NeuralNetConfiguration.ListBuilder networkBase = new NeuralNetConfiguration.Builder()
                .l1(0.001)
                .l2(0.001)
                .weightInit(WeightInit.XAVIER)
                .activation(Activation.SOFTSIGN)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .updater(new Sgd(0.01))
                .list(); // Start layer definition

        // Hidden layers
        for (int i = 0; i < layers.length - 1; i++) {
            networkBase.layer(i, new DenseLayer.Builder()
                    .nIn(layers[i])
                    .nOut(layers[i + 1])
                    .build());
        }

        // Output layer
        networkBase.layer(
                layers.length - 1,
                new OutputLayer.Builder()
                        .nIn(layers[layers.length - 2])
                        .nOut(layers[layers.length - 1])
                        .activation(Activation.TANH)
                        .lossFunction(LossFunctions.LossFunction.MEAN_ABSOLUTE_ERROR)
                        .build()
        );

        this.network = new MultiLayerNetwork(networkBase.build());
        this.network.init();
    }

    @Override
    public void insertWeights(double[] weights) {
        int index = 0; // Indice pour parcourir le tableau `weights`

        for (Layer layer : network.getLayers()) {
            // Récupération des dimensions de la matrice de poids et de biais
            INDArray weightMatrix = layer.getParam(DefaultParamInitializer.WEIGHT_KEY);
            INDArray biasVector = layer.getParam(DefaultParamInitializer.BIAS_KEY);

            int weightSize = (int) weightMatrix.length();
            int biasSize = (int) biasVector.length();

            if (index + weightSize + biasSize > weights.length) {
                throw new IllegalArgumentException("Taille du tableau de poids incorrecte !");
            }

            // Mise à jour des poids
            INDArray newWeights = Nd4j.create(weights, index, weightSize).reshape(weightMatrix.shape());
            layer.setParam(DefaultParamInitializer.WEIGHT_KEY, newWeights);
            index += weightSize;

            // Mise à jour des biais
            INDArray newBiases = Nd4j.create(weights, index, biasSize).reshape(biasVector.shape());
            layer.setParam(DefaultParamInitializer.BIAS_KEY, newBiases);
            index += biasSize;
        }

        if (index != weights.length) {
            throw new IllegalArgumentException("Tous les poids n'ont pas été utilisés !");
        }
    }

    @Override
    public double[] getWeights() {
        int totalWeights = 0;

        // Calcul de la taille totale des poids et biais
        for (Layer layer : network.getLayers()) {
            totalWeights += layer.getParam(DefaultParamInitializer.WEIGHT_KEY).length();
        }

        double[] weightsArray = new double[totalWeights];
        int index = 0;

        // Extraction des poids et biais
        for (Layer layer : network.getLayers()) {
            INDArray weightMatrix = layer.getParam(DefaultParamInitializer.WEIGHT_KEY);

            double[] weightData = weightMatrix.data().asDouble();

            System.arraycopy(weightData, 0, weightsArray, index, weightData.length);
            index += weightData.length;
        }

        return weightsArray;
    }

    @Override
    public double[] compute(double[] inputs) {
        INDArray inputArray = Nd4j.create(inputs);
        INDArray outputArray = network.output(inputArray);
        return outputArray.toDoubleVector();
    }
}
