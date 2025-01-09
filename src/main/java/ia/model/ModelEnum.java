package ia.model;

public enum ModelEnum {
    Random(0),
    DecisionTree(1);

    public int value;

    ModelEnum(int value) {
        this.value = value;
    }

    public static Model getClass(ModelEnum modelEnum) {
        return switch (modelEnum){
            case Random -> new Random();
            case DecisionTree -> new DecisionTree();
        };
    }

    public static ModelEnum getEnum(int value) {
        return switch (value){
            case 0 -> Random;
            case 1 -> DecisionTree;
            default -> throw new IllegalArgumentException("Invalid value");
        };
    }
}
