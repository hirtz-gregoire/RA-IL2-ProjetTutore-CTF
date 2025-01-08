package log;

public abstract class Log {

    public static LogType type = LogType.CONSOLE;

    public abstract void write(String message);
    public abstract void writeln(String message);
    public abstract void writeError(Error e, String message);
}
