package org.supsi.view.preferences;

import java.util.prefs.PreferenceChangeEvent;

/**
 * Represents an event related to preference changes in the application.
 * This interface serves as a marker for all preference-related events.
 * The provided {@code PreferenceChanged} record encapsulates details about a change in preferences.
 */
public interface PreferenceEvent {

    /**
     * Represents a specific event where a preference has been changed.
     * Encapsulates the {@link PreferenceChangeEvent} containing details about the change.
     *
     * @param event the {@link PreferenceChangeEvent} representing the details of the changed preference
     */
    record PreferenceChanged(PreferenceChangeEvent event) implements PreferenceEvent {}
}
