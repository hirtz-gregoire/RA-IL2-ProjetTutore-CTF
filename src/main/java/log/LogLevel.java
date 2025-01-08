package log;

public enum LogLevel {
    NAN(0),
    INFO(1),
    WARNING(2),
    ERROR(3),
    DEBUG(4);

    private final int severity;

    LogLevel(int severity) {
        this.severity = severity;
    }

    public int getSeverity() {return severity;}
}
