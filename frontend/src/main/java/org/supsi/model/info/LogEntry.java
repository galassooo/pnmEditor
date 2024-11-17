package org.supsi.model.info;

/**
 * Represents a log entry containing a type and a message.
 * Each log type is associated with a specific color for display purposes.
 *
 * @param type    the type of the log entry (INFO, ERROR, WARNING, DEBUG)
 * @param message the message of the log entry
 */
public record LogEntry(org.supsi.model.info.LogEntry.LogType type, String message) {

    /**
     * Enum representing the types of log entries.
     * Each type is associated with a specific hex color for display purposes.
     */
    public enum LogType {
        INFO("#61addc"),
        ERROR("#ed5c5c"),
        WARNING("#d18f66"),
        DEBUG("#6fd565");

        private final String hexColor;

        /**
         * Constructs a {@code LogType} with the specified hex color.
         *
         * @param hexColor the hex color associated with the log type
         */
        LogType(String hexColor) {
            this.hexColor = hexColor;
        }

        /**
         * Retrieves the hex color associated with the log type.
         *
         * @return the hex color as a string
         */
        public String getHexColor() {
            return hexColor;
        }
    }

    /**
     * Retrieves the hex color associated with the log entry's type.
     *
     * @return the hex color as a string
     */
    public String getHexColor() {
        return type.getHexColor();
    }
}
