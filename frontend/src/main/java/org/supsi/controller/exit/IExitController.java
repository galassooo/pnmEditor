package org.supsi.controller.exit;

import javafx.stage.Stage;

/**
 * Interface defining the behavior for handling the application exit process.
 * Implementations should manage any cleanup or confirmations required before exiting the application.
 */
public interface IExitController {

    /**
     * Handles the exit process for the application.
     *
     * @param stage The primary {@link Stage} of the application to be closed.
     *              This stage is used to trigger any final actions before the application exits.
     */
    void handleExit(Stage stage);
}
