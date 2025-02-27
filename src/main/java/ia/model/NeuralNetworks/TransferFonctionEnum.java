package ia.model.NeuralNetworks;

import ia.model.*;
import ia.model.NeuralNetworks.MLP.Hyperbolic;
import ia.model.NeuralNetworks.MLP.SoftSign;
import ia.model.NeuralNetworks.MLP.TransferFunction;

public enum TransferFonctionEnum {
    SoftSign(0),
    Hyperbolic(1);

    public int value;

    TransferFonctionEnum(int value) {
        this.value = value;
    }

    public static TransferFunction getClass(TransferFonctionEnum modelEnum) {
        return switch (modelEnum) {
            case SoftSign -> new SoftSign();
            case Hyperbolic -> new Hyperbolic();
        };
    }

    public static TransferFonctionEnum getEnum(int value) {
        return switch (value) {
            case 0 -> SoftSign;
            case 1 -> Hyperbolic;
            default -> throw new IllegalArgumentException("Invalid value");
        };
    }

    public static TransferFunction getTransferFonctionByString(String transferFonctionString) {
        return switch (transferFonctionString) {
            case "SoftSign" -> new SoftSign();
            case "Hyperbolic" -> new Hyperbolic();
            default -> throw new IllegalArgumentException("Invalid transfer fonction");
        };
    }

    public static String getTransferFonctionString(TransferFunction transferFunction) {
        return switch (transferFunction) {
            case SoftSign s -> "SoftSign";
            case Hyperbolic h -> "Hyperbolic";
            default -> throw new IllegalArgumentException("Invalid transfer function");
        };
    }

    public static int getEnumValue(ModelEnum modelEnum) {
        return modelEnum.value;
    }
}
