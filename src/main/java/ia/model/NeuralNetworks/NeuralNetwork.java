package ia.model.NeuralNetworks;

/**
 * Interface representing a neural network
 */
public interface NeuralNetwork {
    /**
     * Takes input and send them into the neural network
     * @param inputs Inputs represented by numbers, /!\ don't forget to normalize /!\
     * @return The output depend on the network shape
     */
    double[] compute(double[] inputs);

    void insertWeights(double[] weights);
    double[] getWeights();
}
