package org.supsi.model.info;

import ch.supsi.application.translations.TranslationsApplication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Represents the logging model for managing log entries of various types.
 * Allows adding logs based on their type and controlling the visibility of each log type.
 *
 * @see LogEntry
 */
public class LoggerModel implements ILoggerModel {

    private static LoggerModel myself;
    private final ObservableList<LogEntry> logs;
    private final TranslationsApplication translationsApp;

    private boolean showDebug = false;
    private boolean showInfo = false;
    private boolean showError = false;
    private boolean showWarning = false;

    /**
     * Constructs a new {@code LoggerModel} instance.
     * Initializes the log storage and retrieves the translations application.
     */
    protected LoggerModel() {
        logs = FXCollections.observableArrayList();
        translationsApp = TranslationsApplication.getInstance();
    }

    /**
     * Retrieves the singleton instance of this class.
     *
     * @return the singleton instance of {@code LoggerModel}
     */
    public static LoggerModel getInstance(){
        if(myself == null){
            myself = new LoggerModel();
        }
        return myself;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear(){
        logs.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addInfo(String info){
        if(showInfo){
            logs.add(new LogEntry(LogEntry.LogType.INFO, translationsApp.translate(info).orElse("N/A")));
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addError(String error){
        if(showError){
            logs.add(new LogEntry(LogEntry.LogType.ERROR, translationsApp.translate(error).orElse("N/A")));
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addWarning(String warning){
        if(showWarning){
            logs.add(new LogEntry(LogEntry.LogType.WARNING, translationsApp.translate(warning).orElse("N/A")));
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addDebug(String debug){
        if(showDebug){
            logs.add(new LogEntry(LogEntry.LogType.DEBUG, translationsApp.translate(debug).orElse("N/A")));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObservableList<LogEntry> getLogs() {
        return logs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setShowDebug(boolean showDebug) {
        this.showDebug = showDebug;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setShowInfo(boolean showInfo) {
        this.showInfo = showInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setShowError(boolean showError) {
        this.showError = showError;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setShowWarning(boolean showWarning) {
        this.showWarning = showWarning;
    }
}
