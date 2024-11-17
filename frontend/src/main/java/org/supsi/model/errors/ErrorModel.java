package org.supsi.model.errors;

import java.util.List;

public class ErrorModel implements IErrorModel {

    private static ErrorModel mySelf;
    private String message;

    protected ErrorModel() {}

    public static ErrorModel getInstance() {
        if (mySelf == null) {
            mySelf = new ErrorModel();

        }
        return mySelf;
    }

    @Override
    public void setMessage(String message) {
        this.message = message.trim();
    }

    @Override
    public void setLogs(List<String> logs) {}

    public String getMessage() {
        return message;
    }
}
