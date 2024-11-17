package org.supsi.model.preferences;

import ch.supsi.application.preferences.PreferencesApplication;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Optional;

/**
 * Represents the model for managing application preferences.
 * Acts as a bridge between the application layer and the preference data storage.
 */
public class PreferencesModel implements IPreferencesModel{

    private static PreferencesModel myself;
    private final PreferencesApplication application;

    /**
     * Constructs a new {@code PreferencesModel} and initializes the application layer reference.
     */
    protected PreferencesModel(){
        application = PreferencesApplication.getInstance();
    }

    /**
     * Retrieves the singleton instance of this class.
     *
     * @return the singleton instance of {@code PreferencesModel}
     */
    public static PreferencesModel getInstance(){
        if(myself==null){
            myself=new PreferencesModel();
        }
        return myself;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Object> getPreference(String key){
        return application.getPreference(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPreference(String key, String value) throws IOException {
        var map = new AbstractMap.SimpleEntry<>(key, value);
        application.setPreference(map);
    }
}
