package ia.model.NeuralNetworks.MLP;

public class SoftSign implements TransferFunction {
    @Override
    public double evaluate(double value) {
        return (value) / (1.0+Math.abs(value));
    }

    @Override
    public double evaluateDer(double value) {
        return 1.0 / Math.pow((1.0+Math.abs(value)), 2);
    }
}
