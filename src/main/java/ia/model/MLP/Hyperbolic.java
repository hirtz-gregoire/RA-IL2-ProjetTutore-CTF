package ia.model.MLP;

public class Hyperbolic implements TransferFunction {
    public Hyperbolic() {

    }

    @Override
    public double evaluate(double value) {
        return Math.tanh(value);
    }

    @Override
    public double evaluateDer(double value) {
        return 1 - Math.pow(value, 2);
    }
}
