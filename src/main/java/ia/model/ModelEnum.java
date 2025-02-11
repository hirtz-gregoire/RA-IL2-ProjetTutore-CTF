package ia.model;

public enum ModelEnum {
    Random(0),
    DecisionTree(1),
    TestRaycast(2);

    public int value;

    ModelEnum(int value) {
        this.value = value;
    }

    public static Model getClass(ModelEnum modelEnum) {
        return switch (modelEnum){
            case Random -> new Random();
            case DecisionTree -> new DecisionTree();
            case TestRaycast -> new TestRaycast();
        };
    }

    public static ModelEnum getEnum(int value) {
        return switch (value){
            case 0 -> Random;
            case 1 -> DecisionTree;
            case 2 -> TestRaycast;
            default -> throw new IllegalArgumentException("Invalid value");
        };
    }

    public static int getEnumValue(ModelEnum modelEnum) {
        return modelEnum.value;
    }
}
