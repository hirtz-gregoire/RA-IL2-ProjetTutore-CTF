package ia.model.NeuralNetworks.MLP;

import ia.model.NeuralNetworks.NeuralNetwork;

import java.util.ArrayList;
import java.util.List;

public class MLP implements NeuralNetwork {
    protected double learningRate = 0.6;
    protected Layer[] layers;
    protected TransferFunction transferFunction;


    /**
     * @param layers           Nb neurones par couches
     * @param learningRate     tx d'apprentissage
     * @param transferFunction Fonction de transfert
     */

    public MLP(int[] layers, double learningRate, TransferFunction transferFunction) {
        this.learningRate = learningRate;
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
     * Rétropropagation
     * @param input  L'entrée courante
     * @param output Sortie souhaitée (apprentissage supervisé !)
     * @return Error différence entre la sortie calculée et la sortie souhaitée
     */

    public double backPropagate(double[] input, double[] output) {
        double[] new_output = compute(input);
        double error;
        int i, j, k;

        // Erreur de sortie
        for (i = 0; i < layers[layers.length - 1].Length; i++) {
            error = output[i] - new_output[i];
            layers[layers.length - 1].Neurons[i].Delta = error * transferFunction.evaluateDer(new_output[i]);
        }

        for (k = layers.length - 2; k >= 0; k--) {
            // Calcul de l'erreur courante pour les couches cachées
            // et mise à jour des Delta de chaque neurone
            for (i = 0; i < layers[k].Length; i++) {
                error = 0.0;
                for (j = 0; j < layers[k + 1].Length; j++)
                    error += layers[k + 1].Neurons[j].Delta * layers[k + 1].Neurons[j].Weights[i];
                layers[k].Neurons[i].Delta = error * transferFunction.evaluateDer(layers[k].Neurons[i].Value);
            }
            // Mise à jour des poids de la couche suivante
            for (i = 0; i < layers[k + 1].Length; i++) {
                for (j = 0; j < layers[k].Length; j++)
                    layers[k + 1].Neurons[i].Weights[j] += learningRate * layers[k + 1].Neurons[i].Delta *
                            layers[k].Neurons[j].Value;
                layers[k + 1].Neurons[i].Bias -= learningRate * layers[k + 1].Neurons[i].Delta;
            }
        }

        // Calcul de l'erreur
        error = 0.0;
        for (i = 0; i < output.length; i++) {
            error += Math.abs(new_output[i] - output[i]);
        }
        error = error / output.length;
        return error;
    }

    /**
     * @return LearningRate
     */
    public double getLearningRate() {
        return learningRate;
    }

    /**
     * maj LearningRate
     * @param rate nouveau LearningRate
     */
    public void setLearningRate(double rate) {
        learningRate = rate;
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
                for (int numWeight = 0; numWeight < layers[numLayer - 1].Length; numWeight++)
                    layers[numLayer].Neurons[numNeuron].Weights[numWeight] = values.removeFirst();
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
            numberOfWeights += layers[numLayer].Length * (layers[numLayer + 1].Length - 1);
        }
        return numberOfWeights;
    }
}