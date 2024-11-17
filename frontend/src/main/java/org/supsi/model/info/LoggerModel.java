package org.supsi.model.info;

import ch.supsi.application.translations.TranslationsApplication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class LoggerModel implements ILoggerModel {

    private static LoggerModel myself;
    private final ObservableList<LogEntry> logs;
    private final TranslationsApplication translationsApp;

    private boolean showDebug = false;
    private boolean showInfo = false;
    private boolean showError = false;
    private boolean showWarning = false;

    protected LoggerModel() {
        logs = FXCollections.observableArrayList();
        translationsApp = TranslationsApplication.getInstance();
    }

    public static LoggerModel getInstance(){
        if(myself == null){
            myself = new LoggerModel();
        }
        return myself;
    }

    @Override
    public void clear(){
        logs.clear();
    }

    @Override
    public void addInfo(String info){
        if(showInfo){
            logs.add(new LogEntry(LogEntry.LogType.INFO, translationsApp.translate(info).orElse("N/A")));
        }

    }

    @Override
    public void addError(String error){
        if(showError){
            logs.add(new LogEntry(LogEntry.LogType.ERROR, translationsApp.translate(error).orElse("N/A")));
        }

    }

    @Override
    public void addWarning(String warning){
        if(showWarning){
            logs.add(new LogEntry(LogEntry.LogType.WARNING, translationsApp.translate(warning).orElse("N/A")));
        }

    }

    @Override
    public void addDebug(String debug){
        if(showDebug){
            logs.add(new LogEntry(LogEntry.LogType.DEBUG, translationsApp.translate(debug).orElse("N/A")));
        }
    }

    @Override
    public ObservableList<LogEntry> getLogs() {
        return logs;
    }

    @Override
    public void setShowDebug(boolean showDebug) {
        this.showDebug = showDebug;
    }

    @Override
    public void setShowInfo(boolean showInfo) {
        this.showInfo = showInfo;
    }

    @Override
    public void setShowError(boolean showError) {
        this.showError = showError;
    }

    @Override
    public void setShowWarning(boolean showWarning) {
        this.showWarning = showWarning;
    }
}
