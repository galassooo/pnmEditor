package org.supsi.model.preferences;

import java.io.IOException;

public interface IPreferencesModel {
    void setPreference(String key, String value) throws IOException;
    Object getPreference(String key);
}
