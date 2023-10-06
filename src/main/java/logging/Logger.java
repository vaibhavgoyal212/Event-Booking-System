package logging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Logger {
    private static Logger logger;
    private final List<LogEntry> log;

    private Logger() {
        log = new ArrayList<>();
    }

    public static Logger getInstance() {
        if (logger == null) {
            logger = new Logger();
        }
        return logger;
    }

    public void logAction(String callerName, Object result) {
        LogEntry newLog = new LogEntry(callerName, result, Collections.emptyMap());
        log.add(newLog);
    }

    public void logAction(String callerName, Object result, Map<String, Object> additionalInfo) {
        LogEntry newLog = new LogEntry(callerName, result, additionalInfo);
        log.add(newLog);
    }

    public List<LogEntry> getLog() {
        return log;
    }

    public void clearLog() {
        log.clear();
    }
}
