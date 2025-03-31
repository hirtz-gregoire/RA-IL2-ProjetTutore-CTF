package ia.model.NeuralNetworks.MLP;

public class Sigmoid implements TransferFunction {

    @Override
    public double evaluate(double value) {
        return (2 / (1 + Math.exp(-value))) - 1;
    }

    @Override
    public double evaluateDer(double value) {
        double sigmoid = (2 / (1 + Math.exp(-value))) - 1;
        return 0.5 * (1 - Math.pow(sigmoid, 2));
    }
}
