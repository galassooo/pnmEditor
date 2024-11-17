package org.supsi.controller.logger;

/**
 * Interface defining the contract for logging operations within the application.
 * Provides methods for adding debug information to the application's logging system.
 */
public interface ILoggerController {

    /**
     * Adds a debug message to the logging system.
     *
     * @param message The debug message to be logged
     */
    void addDebug(String message);
}