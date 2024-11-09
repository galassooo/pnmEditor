package org.supsi.model.errors;

import java.util.List;

public interface IErrorModel {

    void setMessage(String message);
    String getMessage();
    void setLogs(List<String> logs);
}
