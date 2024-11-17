package org.supsi.model.preferences;

import java.io.IOException;
import java.util.Optional;

public interface IPreferencesModel {
    void setPreference(String key, String value) throws IOException;
    Optional<Object> getPreference(String key);
}
