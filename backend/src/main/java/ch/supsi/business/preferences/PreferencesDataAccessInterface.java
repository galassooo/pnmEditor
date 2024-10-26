package ch.supsi.business.preferences;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public interface PreferencesDataAccessInterface {

    Properties getPreferences() throws IOException;

    boolean storePreference(Map.Entry<String, String> preference) throws IOException;
}