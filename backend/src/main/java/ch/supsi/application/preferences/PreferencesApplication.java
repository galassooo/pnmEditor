package ch.supsi.application.preferences;

import ch.supsi.business.preferences.PreferencesBusiness;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/**
 * Provides a high-level interface for managing user preferences in the application.
 * Implements the Singleton pattern to ensure a single instance.
 */
public class PreferencesApplication {

    private static PreferencesApplication myself;
    private final PreferencesBusinessInterface preferencesModel;

    /**
     * Private constructor to enforce the Singleton pattern.
     * Initializes the preferences model from the business layer.
     */
    private PreferencesApplication() {
        this.preferencesModel = PreferencesBusiness.getInstance();
    }

    /**
     * Retrieves the Singleton instance of the PreferencesApplication class.
     *
     * @return the Singleton instance
     */
    public static PreferencesApplication getInstance() {
        if (myself == null) {
            myself = new PreferencesApplication();
        }
        return myself;
    }

    /**
     * Retrieves the value of a preference associated with a given key.
     * Delegates the operation to the business layer.
     *
     * @param key the key of the key-value pair representing the requested preference
     * @return an {@link Optional} containing the value of the preference if present, or empty if not found
     */
    public Optional<Object> getPreference(String key) {
        return this.preferencesModel.getPreference(key);
    }

    /**
     * Stores a preference represented as a key-value pair.
     * Delegates the operation to the business layer for persistence.
     *
     * @param preference the key-value pair representing the preference to be stored
     * @throws IOException if an error occurs while storing the preference
     */
    public void setPreference(Map.Entry<String, String> preference) throws IOException {
        preferencesModel.setPreference(preference);
    }
}
