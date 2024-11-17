package org.supsi.controller.logger;

import ch.supsi.application.preferences.PreferencesApplication;
import org.supsi.model.info.ILoggerModel;
import org.supsi.model.info.LoggerModel;

import java.util.Objects;

public class LoggerController implements ILoggerController{

    private final ILoggerModel model = LoggerModel.getInstance();
    private final PreferencesApplication preferences = PreferencesApplication.getInstance();


    private static LoggerController myself;

    public static LoggerController getInstance() {
        if (myself == null) {
            myself = new LoggerController();
        }
        return myself;
    }

    protected LoggerController() {
        loadProperties();
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
