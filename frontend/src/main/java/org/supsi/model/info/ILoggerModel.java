package org.supsi.model.info;


public interface ILoggerModel extends BasicLogModel{

    void setShowDebug(boolean showDebug);
    void setShowWarning(boolean showWarning);
    void setShowError(boolean showError);
    void setShowInfo(boolean showInfo);
}
