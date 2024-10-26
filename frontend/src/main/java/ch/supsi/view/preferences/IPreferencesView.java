package ch.supsi.view.preferences;

import ch.supsi.model.preferences.IPreferencesModel;

public interface IPreferencesView {
    void setModel(IPreferencesModel model);
    void show();
    void build();
}
