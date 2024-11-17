package ch.supsi.application.preferences;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/**
 * Defines the behavior that a generic preferences model should expose to the application controller.
 * This interface provides methods for managing user preferences and application settings such as language.
 */
public interface PreferencesBusinessInterface {

    /**
     * Retrieves the current language setting of the application.
     *
     * @return an {@link Optional} containing the current language code (e.g., "en-US", "it-Ch"),
     *         or empty if no language is set
     */
    Optional<String> getCurrentLanguage();

    /**
     * Retrieves the value of a specific preference associated with the provided key.
     *
     * @param key the key of the preference to retrieve
     * @return an {@link Optional} containing the value of the preference, or empty if the key does not exist
     */
    Optional<Object> getPreference(String key);

    /**
     * Sets the current language of the application and persists the change.
     *
     * @param language the language code to set (e.g., "en-US", "it-CH")
     * @throws IOException if an error occurs while saving the language preference
     */
    void setCurrentLanguage(String language) throws IOException;

    /**
     * Stores a preference represented as a key-value pair and persists the change.
     *
     * @param preference a {@link Map.Entry} containing the key and value of the preference to store
     * @throws IOException if an error occurs while saving the preference
     */
    void setPreference(Map.Entry<String, String> preference) throws IOException;
}
