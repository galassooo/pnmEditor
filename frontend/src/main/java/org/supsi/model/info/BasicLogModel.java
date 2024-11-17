package org.supsi.model.info;

import javafx.collections.ObservableList;

/**
 * Defines the behavior of a basic logging model for managing log entries of various types.
 * Allows adding, clearing, and retrieving log entries.
 */
public interface BasicLogModel {

    /**
     * Adds an informational log entry.
     *
     * @param info the informational message to be added
     */
    void addInfo(String info);

    /**
     * Adds an error log entry.
     *
     * @param error the error message to be added
     */
    void addError(String error);

    /**
     * Adds a warning log entry.
     *
     * @param warning the warning message to be added
     */
    void addWarning(String warning);

    /**
     * Adds a debug log entry.
     *
     * @param debug the debug message to be added
     */
    void addDebug(String debug);

    /**
     * Clears all log entries from the model.
     */
    @SuppressWarnings("all") //unused
    void clear();

    /**
     * Retrieves the list of log entries as an observable list.
     *
     * @return an {@link ObservableList} of {@link LogEntry} containing all log entries
     */
    ObservableList<LogEntry> getLogs();
}
