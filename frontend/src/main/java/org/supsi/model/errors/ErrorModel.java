package org.supsi.model.errors;

/**
 * A singleton class for managing error messages in the application.
 * Implements the {@link IErrorModel} interface.
 */
public class ErrorModel implements IErrorModel {

    private static ErrorModel mySelf;
    private String message;

    protected ErrorModel() {}

    /**
     * Retrieves the singleton instance of {@code ErrorModel}.
     *
     * @return the singleton instance of {@code ErrorModel}
     */
    public static ErrorModel getInstance() {
        if (mySelf == null) {
            mySelf = new ErrorModel();

        }
        return mySelf;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMessage(String message) {
        this.message = message.trim();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
        return message;
    }
}
