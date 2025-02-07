package ia.model;

import ia.model.NeuralNetworks.ModelNeuralNetwork;

public enum ModelEnum {
    Random(0),
    DecisionTree(1),
    NeuralNetwork(2),
    TestRaycast(3);

    public int value;

    ModelEnum(int value) {
        this.value = value;
    }

    public static Model getClass(ModelEnum modelEnum) {
        return switch (modelEnum){
            case Random -> new Random();
            case DecisionTree -> new DecisionTree();
            case NeuralNetwork -> new ModelNeuralNetwork();
            case TestRaycast -> new TestRaycast();
        };
    }

    public static ModelEnum getEnum(int value) {
        return switch (value){
            case 0 -> Random;
            case 1 -> DecisionTree;
            case 2 -> NeuralNetwork;
            case 3 -> TestRaycast;
            default -> throw new IllegalArgumentException("Invalid value");
        };
    }
}
