package ch.supsi.controller.logger;

import ch.supsi.application.preferences.PreferencesApplication;
import ch.supsi.controller.preferences.PreferencesController;
import ch.supsi.model.info.ILoggerModel;
import ch.supsi.model.info.LoggerModel;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

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
        Object debug = preferences.getPreference("show-debug");
        Object info =  preferences.getPreference("show-info");
        Object warning = preferences.getPreference("show-warning");
        Object error = preferences.getPreference("show-error");

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
