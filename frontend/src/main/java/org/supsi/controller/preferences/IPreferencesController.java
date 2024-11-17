package org.supsi.controller.preferences;

/**
 * Interface defining the contract for preference management operations within the application.
 * Handles the display and interaction with preference-related user interface components.
 */
public interface IPreferencesController {

    /**
     * Displays the preferences popup dialog to the user.
     * This method is responsible for building and showing the preferences interface
     * where users can modify application settings.
     */
    void showPreferencesPopup();
}
