package org.supsi.controller.logger;

import org.supsi.model.info.ILoggerModel;
import org.supsi.model.info.LoggerModel;
import org.supsi.model.preferences.IPreferencesModel;
import org.supsi.model.preferences.PreferencesModel;

import java.util.Objects;

/**
 * The {@code LoggerController} class manages the logging behavior of the application,
 * coordinating between the logger model and user preferences. It is responsible for loading
 * logging preferences and adding log messages.
 */
public class LoggerController implements ILoggerController{

    private static LoggerController myself;
    private final ILoggerModel model;
    private final IPreferencesModel preferences;

    /**
     * Protected constructor to enforce the singleton pattern.
     * Initializes the logger model and preferences, and loads the logging properties.
     */
    protected LoggerController() {
        model = LoggerModel.getInstance();
        preferences = PreferencesModel.getInstance();

        loadProperties();
    }

    /**
     * Returns the singleton instance of the {@code LoggerController}.
     *
     * @return the singleton instance of the {@link LoggerController}.
     */
    public static LoggerController getInstance() {
        if (myself == null) {
            myself = new LoggerController();
        }
        return myself;
    }

    /**
     * Loads the logging preferences (e.g., whether to show debug, info, warning, or error messages)
     * and configures the logger model accordingly.
     */
    private void loadProperties() {
        Object debug = preferences.getPreference("show-debug").orElse(false);
        Object info =  preferences.getPreference("show-info").orElse(false);
        Object warning = preferences.getPreference("show-warning").orElse(false);
        Object error = preferences.getPreference("show-error").orElse(false);

        model.setShowDebug(Boolean.parseBoolean(Objects.toString(debug)));
        model.setShowError(Boolean.parseBoolean(Objects.toString(error)));
        model.setShowInfo(Boolean.parseBoolean(Objects.toString(info)));
        model.setShowWarning(Boolean.parseBoolean(Objects.toString(warning)));

    }

    /**
     * {@inheritDoc}
     * @param message The debug message to be logged
     */
    @Override
    public void addDebug(String message) {
        model.addDebug(message);
    }
}
