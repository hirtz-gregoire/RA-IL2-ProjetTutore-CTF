package ia.model;

import ia.model.NeuralNetworks.ModelNeuralNetwork;

public enum ModelEnum {
    Random(0),
    DecisionTree(1),
    AttackDecisionTree(2),
    DefenseDecisionTree(3),
    NeuralNetwork(4),
    TestRaycast(5);

    public int value;

    ModelEnum(int value) {
        this.value = value;
    }

    public static Model getClass(ModelEnum modelEnum) {
        return switch (modelEnum){
            case Random -> new Random();
            case DecisionTree -> new DecisionTree();
            case AttackDecisionTree -> new AttackDecisionTree();
            case DefenseDecisionTree -> new DefenseDecisionTree();
            case NeuralNetwork -> new ModelNeuralNetwork();
            case TestRaycast -> new TestRaycast();
        };
    }

    public static ModelEnum getEnum(int value) {
        return switch (value){
            case 0 -> Random;
            case 1 -> DecisionTree;
            case 2 -> AttackDecisionTree;
            case 3 -> DefenseDecisionTree;
            case 4 -> NeuralNetwork;
            case 5 -> TestRaycast;
            default -> throw new IllegalArgumentException("Invalid value");
        };
    }

    public static int getEnumValue(ModelEnum modelEnum) {
        return modelEnum.value;
    }
}
