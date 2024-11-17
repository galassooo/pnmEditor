package ch.supsi.business.preferences;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * Interface defining the contract between data access operations and business
 * for managing user preferences.
 * Provides methods to retrieve and store preferences in the application.
 */
public interface PreferencesDataAccessInterface {

    /**
     * Retrieves all user preferences stored in the application.
     *
     * @return a {@link Properties} object containing all the user preferences as key-value pairs
     */
    Properties getPreferences();

    /**
     * Stores a preference in the application.
     *
     * @param preference a {@link Map.Entry} representing the key-value pair of the preference to store
     * @return {@code true} if the operation was successful, {@code false} otherwise
     * @throws IOException if an error occurs while storing the preference
     */
    boolean storePreference(Map.Entry<String, String> preference) throws IOException;
}