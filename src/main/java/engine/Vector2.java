package engine;

/**
 * Record Class (x,y) 2D coordinate
 * @param x
 * @param y
 */
public record Vector2(double x, double y) {

    public static final Vector2 ZERO = new Vector2(0, 0);
    public static final Vector2 ONE = new Vector2(1, 1);
    public static final Vector2 UP = new Vector2(0, -1);
    public static final Vector2 DOWN = new Vector2(0, 1);
    public static final Vector2 LEFT = new Vector2(-1, 0);
    public static final Vector2 RIGHT = new Vector2(1, 0);

    public static Vector2 fromAngle(double angle) {
        var radii = Math.toRadians(angle);
        return new Vector2(Math.cos(radii), Math.sin(radii));
    }

    public double angle(Vector2 other) {
        return (other.getAngle() - getAngle() + 360) % 360;
    }

    public double signedAngle(Vector2 other) {
        return angle(other) - 180;
    }

    public double dot(Vector2 other) {
        return x * other.x + y * other.y;
    }

    public double distance(Vector2 other) {
        var vect = sub(other);
        return vect.length();
    }

    public Vector2 add(Vector2 other) {
        return new Vector2(x() + other.x(), y() + other.y());
    }
    public Vector2 add(double value) {
        return new Vector2(x() + value, y() + value);
    }

    public Vector2 sub(Vector2 other) {
        return new Vector2(x() - other.x(), y() - other.y());
    }

    public Vector2 subtract(double value) {
        return new Vector2(x() - value, y() - value);
    }

    public Vector2 multiply(Vector2 other) {
        return new Vector2(x() * other.x(), y() * other.y());
    }

    public Vector2 multiply(double value) {
        return new Vector2(x() * value, y() * value);
    }

    public Vector2 divide(Vector2 other) {
        return new Vector2(x() / other.x(), y() / other.y());
    }

    public Vector2 divide(double value) {
        return new Vector2(x() / value, y() / value);
    }

    public Vector2 normalized() {
        var length = length();
        return new Vector2(x / length, y / length);
    }

    public double length() {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    public double getAngle() {
        return Math.toDegrees(Math.atan2(y, x));
    }
}
