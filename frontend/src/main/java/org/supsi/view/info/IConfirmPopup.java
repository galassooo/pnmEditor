package org.supsi.view.info;

/**
 * Represents a contract for confirmation popup in the application.
 * Provides a method to display a confirmation dialog and capture the user's response.
 */
public interface IConfirmPopup {

    /**
     * Displays a confirmation dialog and waits for the user's input.
     *
     * @return {@code true} if the user confirms the action, {@code false} otherwise
     */
    boolean showConfirmationDialog();
}
