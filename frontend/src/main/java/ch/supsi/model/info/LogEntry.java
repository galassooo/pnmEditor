package ch.supsi.model.info;

public class LogEntry {

    public enum LogType {
        INFO("#61addc"),
        ERROR("#ed5c5c"),
        WARNING("#d18f66"),
        DEBUG("#6fd565");

        private final String hexColor;

        LogType(String hexColor) {
            this.hexColor = hexColor;
        }

        public String getHexColor() {
            return hexColor;
        }
    }

    private final LogType type;
    private final String message;

    public LogEntry(LogType type, String message) {
        this.type = type;
        this.message = message;
    }

    public LogType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public String getHexColor() {
        return type.getHexColor();
    }
}
