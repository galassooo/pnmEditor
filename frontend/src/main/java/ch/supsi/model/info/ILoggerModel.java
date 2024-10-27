package ch.supsi.model.info;

import javafx.collections.ObservableList;

public interface ILoggerModel {
    void addInfo(String info);
    void addError(String error);
    void addWarning(String warning);
    void addDebug(String debug);
    void clear();
    ObservableList<LogEntry> getLogs();

    void setShowDebug(boolean showDebug);
}
