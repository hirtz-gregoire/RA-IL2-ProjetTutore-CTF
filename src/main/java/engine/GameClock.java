package engine;

public record GameClock(long start) {
    public GameClock() {
        this(System.currentTimeMillis());
    }

    public long millis() {
        return System.currentTimeMillis() - start;
    }
}
