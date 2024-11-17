package org.supsi.model.about;

/**
 * Defines the behavior for managing application metadata such as developer information,
 * release date, and version.
 */
public interface IAboutModel {

    /**
     * Sets the developer's name or identifier.
     *
     * @param developer the developer's name or identifier
     */
    void setDeveloper(String developer);

    /**
     * Retrieves the developer's name or identifier.
     *
     * @return the developer's name or identifier
     */
    String getDeveloper();

    /**
     * Sets the release date of the application.
     *
     * @param date the release date in a specified format
     */
    void setDate(String date);

    /**
     * Retrieves the release date of the application.
     *
     * @return the release date in a specified format
     */
    String getDate();

    /**
     * Sets the version of the application.
     *
     * @param version the version string
     */
    void setVersion(String version);

    /**
     * Retrieves the version of the application.
     *
     * @return the version string
     */
    String getVersion();
}
