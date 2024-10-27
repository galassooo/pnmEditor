package ch.supsi.controller.logger;

import ch.supsi.model.info.ILoggerModel;
import ch.supsi.model.info.LoggerModel;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class LoggerController implements ILoggerController{

    private final ILoggerModel model = LoggerModel.getInstance();

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
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config/application.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find application.properties");
                return;
            }

            properties.load(input);
            String showDebugProperty = properties.getProperty("show_debug");
            if (showDebugProperty != null) {
                boolean showDebug = Boolean.parseBoolean(showDebugProperty);
                model.setShowDebug(showDebug);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void addDebug(String message) {
        model.addDebug(message);
    }
}
