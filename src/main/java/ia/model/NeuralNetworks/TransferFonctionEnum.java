package ia.model.NeuralNetworks;

import ia.model.*;
import ia.model.NeuralNetworks.MLP.Hyperbolic;
import ia.model.NeuralNetworks.MLP.Sigmoid;
import ia.model.NeuralNetworks.MLP.TransferFunction;

public enum TransferFonctionEnum {
    Sigmoid(0),
    Hyperbolic(1);

    public int value;

    TransferFonctionEnum(int value) {
        this.value = value;
    }

    public static TransferFunction getClass(TransferFonctionEnum modelEnum) {
        return switch (modelEnum) {
            case Sigmoid -> new Sigmoid();
            case Hyperbolic -> new Hyperbolic();
        };
    }

    public static TransferFonctionEnum getEnum(int value) {
        return switch (value) {
            case 0 -> Sigmoid;
            case 1 -> Hyperbolic;
            default -> throw new IllegalArgumentException("Invalid value");
        };
    }

    public static TransferFunction getTransferFonctionByString(String transferFonctionString) {
        return switch (transferFonctionString) {
            case "Sigmoid" -> new Sigmoid();
            case "Hyperbolic" -> new Hyperbolic();
            default -> throw new IllegalArgumentException("Invalid transfer fonction");
        };
    }

    public static int getEnumValue(ModelEnum modelEnum) {
        return modelEnum.value;
    }
}
