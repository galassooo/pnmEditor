package org.supsi.controller.errors;

/**
 * Defines the behavior that a generic error controller should expose.
 * Used to display error messages to the user.
 */
public interface IErrorController {

    /**
     * Displays an error message to the user.
     *
     * @param message The error message to be shown.
     */
    void showError(String message);
}
