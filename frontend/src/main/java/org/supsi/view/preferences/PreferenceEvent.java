package org.supsi.view.preferences;

import java.util.prefs.PreferenceChangeEvent;

public interface PreferenceEvent {
    record PreferenceChanged(PreferenceChangeEvent event) implements PreferenceEvent {}
}
