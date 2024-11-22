package engine.agent;

public record Action(double rotationRatio, double speedRatio) {
    // TODO: attention rotationRatio & speedRatio entre [-1, 1] uniquement
}
