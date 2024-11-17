package ch.supsi.business.preferences;


import ch.supsi.application.preferences.PreferencesBusinessInterface;
import ch.supsi.dataaccess.preferences.PreferencesDataAccess;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

public class PreferencesBusiness implements PreferencesBusinessInterface {

    /* self reference */
    private static PreferencesBusiness myself;

    /* data access reference */
    private final PreferencesDataAccessInterface preferencesDao;

    /* field */
    private Properties userPreferences;

    /* constructor */
    protected PreferencesBusiness() {
        this.preferencesDao = PreferencesDataAccess.getInstance();
    }

    public static PreferencesBusiness getInstance() {
        if (myself == null) {
            myself = new PreferencesBusiness();
        }

        return myself;
    }

    /**
     * Delegates the retrieval of the current language to the data access layer
     *
     * @return the current language of the program
     */
    @Override
    public Optional<String> getCurrentLanguage() {
        userPreferences = preferencesDao.getPreferences();
        return Optional.of(userPreferences.getProperty("language-tag"));
    }

    /**
     * Delegates the storage of the language to the data access layer
     *
     * @param languageCode the value of the language preference to be set
     */
    @Override
    public void setCurrentLanguage(String languageCode) throws IOException {
        Map.Entry<String, String> languagePreference = Map.entry("language-tag", languageCode);
        this.preferencesDao.storePreference(languagePreference);
    }

    /**
     * Delegates the retrieval of the preference associated with a given key
     *
     * @param key the key associated with the preference in a key-value pair
     * @return the value associated with the given key
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
     * Delegates the storage of the key-value pair associated with a preference to the data access layer
     *
     * @param preference the key-value pair representing the preference to be stored
     */
    @Override
    public void setPreference(Map.Entry<String, String> preference) throws IOException {
        this.preferencesDao.storePreference(preference);
    }
}
