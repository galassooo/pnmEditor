package org.supsi.view.preferences;

import java.util.prefs.PreferenceChangeListener;

public interface PreferenceChangedEvent {

    void registerListener(PreferenceChangeListener listener);
    void deregisterListener(PreferenceChangeListener listener);
}
