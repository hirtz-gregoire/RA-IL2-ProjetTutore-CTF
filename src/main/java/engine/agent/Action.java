package engine.agent;

public record Action(double rotationRatio, double speedRatio) {
    // TODO: attention rotationRatio & speedRatio entre [-1, 1] uniquement

    public Action {
        rotationRatio = Math.clamp(rotationRatio, -1, 1);
        speedRatio = Math.clamp(speedRatio, -1, 1);
    }
}
