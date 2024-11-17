package org.supsi.model.info;

/**
 * Extends the {@link BasicLogModel} to add functionality for managing the visibility of different log levels.
 * Provides methods to enable or disable the display of specific types of log messages.
 */
public interface ILoggerModel extends BasicLogModel{

    /**
     * Sets whether debug log entries should be displayed.
     *
     * @param showDebug {@code true} to display debug messages, {@code false} otherwise
     */
    void setShowDebug(boolean showDebug);

    /**
     * Sets whether warning log entries should be displayed.
     *
     * @param showWarning {@code true} to display warning messages, {@code false} otherwise
     */
    void setShowWarning(boolean showWarning);

    /**
     * Sets whether error log entries should be displayed.
     *
     * @param showError {@code true} to display error messages, {@code false} otherwise
     */
    void setShowError(boolean showError);

    /**
     * Sets whether informational log entries should be displayed.
     *
     * @param showInfo {@code true} to display informational messages, {@code false} otherwise
     */
    void setShowInfo(boolean showInfo);
}
