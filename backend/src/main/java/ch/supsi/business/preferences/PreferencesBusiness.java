package ch.supsi.business.preferences;


import ch.supsi.application.preferences.PreferencesBusinessInterface;
import ch.supsi.dataaccess.preferences.PreferencesDataAccess;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * Provides business logic for managing user preferences in the application.
 * This class interacts with the data access layer to retrieve and store preferences,
 * including the current language setting and other key-value preferences.
 * Implements the Singleton pattern to ensure a single instance is used across the application.
 */
public class PreferencesBusiness implements PreferencesBusinessInterface {

    private static PreferencesBusiness myself;
    private final PreferencesDataAccessInterface preferencesDao;
    private Properties userPreferences;


    protected PreferencesBusiness() {
        this.preferencesDao = PreferencesDataAccess.getInstance();
    }

    /**
     * Retrieves the Singleton instance of {@link PreferencesBusiness}.
     *
     * @return the Singleton instance
     */

    public static PreferencesBusiness getInstance() {
        if (myself == null) {
            myself = new PreferencesBusiness();
        }

        return myself;
    }

    /**
     * Retrieves the current language preference of the application.
     *
     * @return an {@link Optional} containing the current language tag if available, or empty otherwise
     */
    @Override
    public Optional<String> getCurrentLanguage() {
        userPreferences = preferencesDao.getPreferences();
        return Optional.of(userPreferences.getProperty("language-tag"));
    }

    /**
     * Sets the current language preference for the application.
     *
     * @param languageCode the language code (e.g., "en", "it") to set as the current language
     * @throws IOException if an error occurs while storing the preference
     */
    @Override
    public void setCurrentLanguage(String languageCode) throws IOException {
        Map.Entry<String, String> languagePreference = Map.entry("language-tag", languageCode);
        this.preferencesDao.storePreference(languagePreference);
    }

    /**
     * Retrieves the preference associated with the specified key.
     *
     * @param key the key for the desired preference
     * @return an {@link Optional} containing the preference value if found, or empty if the key is invalid or not present
     */
    @Override
    public Optional<Object> getPreference(String key) {
        this.userPreferences = preferencesDao.getPreferences();
        if (key == null || key.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(userPreferences.get(key));
    }


    /**
     * Sets a preference in the application by storing the specified key-value pair.
     *
     * @param preference the key-value pair representing the preference to store
     * @throws IOException if an error occurs while storing the preference
     */
    @Override
    public void setPreference(Map.Entry<String, String> preference) throws IOException {
        this.preferencesDao.storePreference(preference);
    }
}
