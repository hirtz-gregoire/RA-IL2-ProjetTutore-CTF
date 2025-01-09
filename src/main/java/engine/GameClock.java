package engine;

public record GameClock(long start) {
    public GameClock() {this(System.nanoTime());}

    public double millis() {return (System.nanoTime() - start) / 1_000_000.0;}
}