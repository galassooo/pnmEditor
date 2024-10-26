package ch.supsi.application.preferences;

import java.io.IOException;
import java.util.Map;

/**
 * Defines the behaviour that a generic preferences model should expose to the controller
 */
public interface PreferencesBusinessInterface {

    String getCurrentLanguage() throws IOException;

    void setCurrentLanguage(String language) throws IOException;

    Object getPreference(String key) throws IOException;

    void setPreference(Map.Entry<String, String> preference) throws IOException;
}

