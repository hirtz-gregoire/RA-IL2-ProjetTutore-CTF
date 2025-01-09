package engine.agent;

/**
 * Record Class for ration speed and rotation
 * @param rotationRatio
 * @param speedRatio
 */
public record Action(double rotationRatio, double speedRatio) {

    public Action {
        if (rotationRatio < -1 || rotationRatio > 1)
            throw new IllegalArgumentException("rotationRatio must be between -1 and 1. Provided: " + rotationRatio);
        if (speedRatio < -1 || speedRatio > 1)
            throw new IllegalArgumentException("speedRatio must be between -1 and 1. Provided: " + speedRatio);
    }
}

