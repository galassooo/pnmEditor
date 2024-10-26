package ch.supsi.model.preferences;

import ch.supsi.application.preferences.PreferencesApplication;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public class PreferencesModel implements IPreferencesModel{

    private static PreferencesModel myself;
    private final PreferencesApplication application = PreferencesApplication.getInstance();

    public static PreferencesModel getInstance(){
        if(myself==null){
            myself=new PreferencesModel();
        }
        return myself;
    }

    protected PreferencesModel(){}


    public Object getPreference(String key){
        return application.getPreference(key);
    }

    @Override
    public void setPreference(String key, String value) throws IOException {
        var map = new AbstractMap.SimpleEntry<>(key, value);
        application.setPreference(map);
    }
}
