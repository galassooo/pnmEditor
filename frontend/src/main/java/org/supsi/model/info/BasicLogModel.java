package org.supsi.model.info;

import javafx.collections.ObservableList;

public interface BasicLogModel {
    void addInfo(String info);
    void addError(String error);
    void addWarning(String warning);
    void addDebug(String debug);
    @SuppressWarnings("all") //unused
    void clear();
    ObservableList<LogEntry> getLogs();
}
