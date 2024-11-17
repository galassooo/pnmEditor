package org.supsi.model.preferences;

import java.io.IOException;
import java.util.Optional;

/**
 * Defines the behavior of a preferences model for managing application preferences.
 * Allows setting and retrieving preferences using key-value pairs.
 */
public interface IPreferencesModel {

    /**
     * Sets a preference value associated with the given key.
     *
     * @param key   the key of the preference to be set
     * @param value the value to be associated with the given key
     * @throws IOException if there is an error while saving the preference
     */
    void setPreference(String key, String value) throws IOException;

    /**
     * Retrieves the value of a preference associated with the given key.
     *
     * @param key the key of the preference to retrieve
     * @return an {@link Optional} containing the value of the preference if found, or {@code Optional.empty()} if not found
     */
    Optional<Object> getPreference(String key);
}
