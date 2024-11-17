package org.supsi.model.info;

import ch.supsi.application.translations.TranslationsApplication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class LoggerModel implements ILoggerModel {

    private static LoggerModel myself;

    private ObservableList<LogEntry> logs = FXCollections.observableArrayList();

    private TranslationsApplication translationsApp = TranslationsApplication.getInstance();

    private boolean showDebug = false;
    private boolean showInfo = false;
    private boolean showError = false;
    private boolean showWarning = false;

    public static LoggerModel getInstance(){
        if(myself == null){
            myself = new LoggerModel();
        }
        return myself;
    }

    public void clear(){
        logs.clear();
    }

    public void addInfo(String info){
        if(showInfo){
            logs.add(new LogEntry(LogEntry.LogType.INFO, translationsApp.translate(info).orElse("N/A")));
        }

    }

    public void addError(String error){
        if(showError){
            logs.add(new LogEntry(LogEntry.LogType.ERROR, translationsApp.translate(error).orElse("N/A")));
        }

    }

    public void addWarning(String warning){
        if(showWarning){
            logs.add(new LogEntry(LogEntry.LogType.WARNING, translationsApp.translate(warning).orElse("N/A")));
        }

    }

    public void addDebug(String debug){
        if(showDebug){
            logs.add(new LogEntry(LogEntry.LogType.DEBUG, translationsApp.translate(debug).orElse("N/A")));
        }
    }

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
