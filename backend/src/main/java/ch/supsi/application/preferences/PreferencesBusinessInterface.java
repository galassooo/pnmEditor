package ch.supsi.application.preferences;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/**
 * Defines the behaviour that a generic preferences model should expose to the controller
 */
public interface PreferencesBusinessInterface {

    Optional<String> getCurrentLanguage();

    void setCurrentLanguage(String language) throws IOException;

    Optional<Object> getPreference(String key);

    void setPreference(Map.Entry<String, String> preference) throws IOException;
}

