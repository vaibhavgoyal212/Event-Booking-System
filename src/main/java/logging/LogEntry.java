package logging;

import java.util.Map;

public class LogEntry {
    private final String callerName;
    private final Object result;
    private final Map<String, Object> additionalInfo;

    LogEntry(String callerName, Object result, Map<String, Object> additionalInfo) {
        this.callerName = callerName;
        this.result = result;
        this.additionalInfo = additionalInfo;
    }

    public String getResult() {
        return result.toString();
    }

    @Override
    public String toString() {
        return "LogEntry{" +
                "callerName='" + callerName + '\'' +
                ", result=" + result +
                ", additionalInfo=" + additionalInfo +
                '}';
    }
}
