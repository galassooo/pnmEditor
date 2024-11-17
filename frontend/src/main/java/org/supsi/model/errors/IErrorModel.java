package org.supsi.model.errors;

/**
 * Defines the data for storing error messages in the application.
 * Provides methods to set and retrieve messages.
 */
public interface IErrorModel {

    /**
     * Sets the error message.
     *
     * @param message the error message to set
     */
    void setMessage(String message);

    /**
     * Retrieves the current error message.
     *
     * @return the current error message
     */
    String getMessage();
}
