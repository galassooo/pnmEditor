package ch.supsi.model.info;

import ch.supsi.application.translations.TranslationsApplication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class LoggerModel implements ILoggerModel {

    private static LoggerModel myself;

    private ObservableList<LogEntry> logs = FXCollections.observableArrayList();

    private TranslationsApplication translationsApp = TranslationsApplication.getInstance();

    private boolean showDebug = false;

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
        logs.add(new LogEntry(LogEntry.LogType.INFO, translationsApp.translate(info)));
    }

    public void addError(String error){
        logs.add(new LogEntry(LogEntry.LogType.ERROR, translationsApp.translate(error)));
    }

    public void addWarning(String warning){
        logs.add(new LogEntry(LogEntry.LogType.WARNING, translationsApp.translate(warning)));
    }

    public void addDebug(String debug){
        if(showDebug){
            logs.add(new LogEntry(LogEntry.LogType.DEBUG, translationsApp.translate(debug)));
        }
    }

    public ObservableList<LogEntry> getLogs() {
        return logs;
    }

    @Override
    public void setShowDebug(boolean showDebug) {
        this.showDebug = showDebug;
    }
}
