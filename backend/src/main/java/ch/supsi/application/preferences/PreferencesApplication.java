package ch.supsi.application.preferences;
import ch.supsi.business.preferences.PreferencesBusiness;

import java.io.IOException;
import java.util.Map;

public class PreferencesApplication {

    /* self reference */
    private static PreferencesApplication myself;

    /* business references */
    private final PreferencesBusinessInterface preferencesModel;


    private PreferencesApplication() {
        this.preferencesModel = PreferencesBusiness.getInstance();
    }

    public static PreferencesApplication getInstance() {
        if (myself == null) {
            myself = new PreferencesApplication();
        }
        return myself;
    }


    /**
     * Delegates the retrieval of the value associated with a given key to the model
     *
     * @param key the key of the key - value pair of the requested preference
     * @return an object representing the value of the preference
     */
    public Object getPreference(String key) throws IOException {
        return this.preferencesModel.getPreference(key);
    }

    /**
     * Delegates the storage of the key-value pair associated with a preference to the model
     *
     * @param preference the key-value pair representing the preference to be stored
     */
    public void setPreference(Map.Entry<String, String> preference) throws IOException {
        preferencesModel.setPreference(preference);
    }
}


