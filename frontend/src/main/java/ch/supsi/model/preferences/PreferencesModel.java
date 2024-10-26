package ch.supsi.model.preferences;

public class PreferencesModel implements IPreferencesModel{

    private static PreferencesModel myself;


    public static PreferencesModel getInstance() {
        if (myself == null) {
            myself = new PreferencesModel();
        }
        return myself;
    }
}
