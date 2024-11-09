package org.supsi.view.preferences;

import org.supsi.model.translations.ITranslationsModel;

public interface IPreferencesView {
    void setModel(ITranslationsModel model);
    void show();
    void build();
}
