package ia.model.NeuralNetworks.MLP;

import ia.model.NeuralNetworks.NeuralNetwork;

import java.util.ArrayList;
import java.util.List;

public class MLP implements NeuralNetwork {
    protected Layer[] layers;
    protected TransferFunction transferFunction;

    /**
     * @param layers           Nb neurones par couches
     * @param transferFunction Fonction de transfert
     */

    public MLP(int[] layers, TransferFunction transferFunction) {
        this.transferFunction = transferFunction;

        this.layers = new Layer[layers.length];
        for (int i = 0; i < layers.length; i++) {
            if (i != 0) {
                this.layers[i] = new Layer(layers[i], layers[i - 1]);
            } else {
                this.layers[i] = new Layer(layers[i], 0);
            }
        }
    }

    /**
     * Réponse à une entrée
     * @param inputs l'entrée testée
     * @return résultat de l'exécution
     */
    public double[] compute(double[] inputs) {
        int i, j, k;
        double new_value;

        double[] outputs = new double[layers[layers.length - 1].Length];

//        System.out.println(inputs.length);
//        System.out.print(getNumberOfWeight());
        // input en entrée du réseau
        for (i = 0; i < layers[0].Length; i++) {
            layers[0].Neurons[i].Value = inputs[i];
        }

        // calculs couches cachées et sortie
        for (k = 1; k < layers.length; k++) {
            for (i = 0; i < layers[k].Length; i++) {
                new_value = 0.0;
                for (j = 0; j < layers[k - 1].Length; j++)
                    new_value += layers[k].Neurons[i].Weights[j] * layers[k - 1].Neurons[j].Value;

                new_value -= layers[k].Neurons[i].Bias;
                layers[k].Neurons[i].Value = transferFunction.evaluate(new_value);
            }
        }

        // Renvoyer sortie
        for (i = 0; i < layers[layers.length - 1].Length; i++) {
            outputs[i] = layers[layers.length - 1].Neurons[i].Value;
        }

//        System.out.println("\nINPUTS : ");
//        for (double input : inputs) {
//            System.out.print(input + " ");
//        }
//        System.out.println("\nOUTPUTS : ");
//        for (double output : outputs) {
//            System.out.print(output + " ");
//        }

        return outputs;
    }

    /**
     * maj fonction de tranfert
     * @param fun nouvelle fonction de tranfert
     */
    public void setTransferFunction(TransferFunction fun) {
        transferFunction = fun;
    }

    /**
     * @return Taille couche d'entrée
     */
    public int getInputLayerSize() {
        return layers[0].Length;
    }

    /**
     * @return Taille couche de sortie
     */
    public int getOutputLayerSize() {
        return layers[layers.length - 1].Length;
    }

    @Override
    public void insertWeights(double[] weights) {
        //Conversion double[] en List<Double>
        List<Double> values = new ArrayList<>();
        for (double weight : weights) {
            values.add(weight);
        }

        for (int numLayer = 1; numLayer < layers.length; numLayer++) {
            for (int numNeuron = 0; numNeuron < layers[numLayer].Length; numNeuron++) {
                for (int numWeight = 0; numWeight < layers[numLayer - 1].Length; numWeight++) {
                    layers[numLayer].Neurons[numNeuron].Weights[numWeight] = values.removeFirst();
                }
                layers[numLayer].Neurons[numNeuron].Bias = values.removeFirst();
            }
        }
    }

    @Override
    public double[] getWeights() {
        List<Double> weights = new ArrayList<>();

        for (int numLayer = 1; numLayer < layers.length; numLayer++) {
            for (int numNeuron = 0; numNeuron < layers[numLayer].Length; numNeuron++) {
                for (int numWeight = 0; numWeight < layers[numLayer - 1].Length; numWeight++)
                    weights.add(layers[numLayer].Neurons[numNeuron].Weights[numWeight]);
                weights.add(layers[numLayer].Neurons[numNeuron].Bias);
            }
        }

        //Conversion List<Double> en double[]
        double[] values = new double[weights.size()];
        for (int i = 0; i < weights.size(); i++) {
            values[i] = weights.get(i);
        }
        return values;
    }

    public int getNumberOfWeight() {
        int numberOfWeights = 0;
        for (int numLayer = 1; numLayer < layers.length; numLayer++) {
            numberOfWeights += layers[numLayer].Length * (layers[numLayer - 1].Length + 1);
        }
        return numberOfWeights;
    }

    public static int getNumberOfWeight(List<Integer> layers) {
        int numberOfWeights = 0;
        for (int numLayer = 1; numLayer < layers.size(); numLayer++) {
            numberOfWeights += layers.get(numLayer) * (layers.get(numLayer - 1) + 1);
        }
        return numberOfWeights;
    }

    public Layer[] getLayers() {
        return layers;
    }
}