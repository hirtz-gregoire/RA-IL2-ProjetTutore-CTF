package log.type;

import log.Log;
import log.LogLevel;

public class Console extends Log {


    @Override
    protected void write(LogLevel level, String message, Error... errors) {

        System.out.printf("[%s] %s", level, message);

    }
}
