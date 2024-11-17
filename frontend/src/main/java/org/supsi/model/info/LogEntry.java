package org.supsi.model.info;

public record LogEntry(org.supsi.model.info.LogEntry.LogType type, String message) {

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

    public String getHexColor() {
        return type.getHexColor();
    }
}
