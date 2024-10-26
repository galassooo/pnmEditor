package ch.supsi.view.preferences;

import ch.supsi.model.preferences.IPreferencesModel;
import ch.supsi.model.translations.ITranslationsModel;

public interface IPreferencesView {
    void setModel(ITranslationsModel model);
    void show();
    void build();
}
