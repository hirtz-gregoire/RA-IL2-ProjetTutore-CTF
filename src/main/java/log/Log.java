package log;

public abstract class Log {

    public final LogType type =  LogType.CONSOLE;
    public final LogLevel level = LogLevel.DEBUG;

    public void log(LogLevel l, String message, Error... errors){
        if (l.getSeverity() <= level.getSeverity()){
            write(l, message, errors);
        }
    }

    protected abstract void write(LogLevel level, String message, Error... errors);
}
