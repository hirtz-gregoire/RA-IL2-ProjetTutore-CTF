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

    /**
     * Create a vector from a given angle
     * @param angle The angle in degree [0-360]
     * @return The resulting angle vector
     */
    public static Vector2 fromAngle(double angle) {
        var radii = Math.toRadians(angle);
        return new Vector2(Math.cos(radii), Math.sin(radii));
    }

    /**
     * Get the difference of angle between two vectors
     * @param other The vector to check against this
     * @return Return the angle in degree [0-360] (using other.position - this.position)
     */
    public double angle(Vector2 other) {
        // Don't use built-in function to not create useless instances
        var x = other.x() - x();
        var y = other.y() - y();
        return Math.toDegrees(Math.atan2(y, x));
    }

    /**
     * Get the difference of angle between two vectors
     * @param other The vector to check against this
     * @return Return the angle in degree [-180 - 180] (using other.angle - this.angle)
     */
    public double signedAngle(Vector2 other) {
        var angle = angle(other) - 180;
        return (180 - Math.abs(angle)) * Math.signum(angle);
    }

    /**
     * Get the dot product of two vectors
     * @param other The other vector to use
     * @return Then dot product of the two vectors, -1 for two opposit-facing normalized vectors, 0 for perpendicular vectors
     */
    public double dot(Vector2 other) {
        return x * other.x + y * other.y;
    }

    /**
     * Get the distance between two positions
     * @param other The other positions
     * @return The distance between the two positions
     */
    public double distance(Vector2 other) {
        // Don't use built-in function to not create useless instances
        var x = other.x() - x();
        var y = other.y() - y();
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    public Vector2 add(Vector2 other) {
        return new Vector2(x() + other.x(), y() + other.y());
    }

    public Vector2 add(double value) {
        return new Vector2(x() + value, y() + value);
    }

    public Vector2 subtract(Vector2 other) {
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

    /**
     * Normalize the Vector to get a vector of length 1
     * @return A vector of same direction but size 1
     */
    public Vector2 normalized() {
        var length = length();
        if(length == 0) return new Vector2(0, 0);
        return new Vector2(x / length, y / length);
    }

    /**
     * Get the length of the vector. If you want to compute distance between two vector, please use distance(Vector) instead of subtract(other).length()
     * @return A positive value representing the length of the vector
     */
    public double length() {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    /**
     * Convert the vector into a 360 angle
     * @return The angle of the vector in degree
     */
    public double getAngle() {
        double angle = Math.toDegrees(Math.atan2(y, x));
        return (angle < 0) ? angle + 360 : angle;
    }

    public Vector2 copy(){
        return new Vector2(x, y);
    }
}
