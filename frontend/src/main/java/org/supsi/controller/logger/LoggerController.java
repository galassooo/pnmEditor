package org.supsi.controller.logger;

import org.supsi.model.info.ILoggerModel;
import org.supsi.model.info.LoggerModel;
import org.supsi.model.preferences.IPreferencesModel;
import org.supsi.model.preferences.PreferencesModel;

import java.util.Objects;

public class LoggerController implements ILoggerController{

    private static LoggerController myself;
    private final ILoggerModel model;
    private final IPreferencesModel preferences;

    protected LoggerController() {
        model = LoggerModel.getInstance();
        preferences = PreferencesModel.getInstance();

        loadProperties();
    }

    public static LoggerController getInstance() {
        if (myself == null) {
            myself = new LoggerController();
        }
        return myself;
    }

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

    @Override
    public void addDebug(String message) {
        model.addDebug(message);
    }
}
