package ch.supsi.controller.preferences;

import ch.supsi.controller.errors.ErrorController;
import ch.supsi.model.info.ILoggerModel;
import ch.supsi.model.info.LoggerModel;
import ch.supsi.model.preferences.IPreferencesModel;
import ch.supsi.model.preferences.PreferencesModel;
import ch.supsi.model.translations.ITranslationsModel;
import ch.supsi.model.translations.TranslationModel;
import ch.supsi.view.preferences.IPreferencesView;
import ch.supsi.view.preferences.PreferenceChangedEvent;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.net.URL;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

public class PreferencesController implements IPreferencesController, PreferenceChangeListener {

    private static PreferencesController mySelf;

    private IPreferencesView view;
    private final IPreferencesModel model = PreferencesModel.getInstance();
    private final ILoggerModel loggerModel = LoggerModel.getInstance();
    private final ITranslationsModel translationsModel = TranslationModel.getInstance();

    public static PreferencesController getInstance() {
        if (mySelf == null) {
            mySelf = new PreferencesController();
        }
        return mySelf;
    }

    public PreferencesController() {
            URL fxmlUrl = getClass().getResource("/layout/PreferencesPopup.fxml");
            if (fxmlUrl == null) {
                return;
            }

            try {
                FXMLLoader loader = new FXMLLoader(fxmlUrl, translationsModel.getUiBundle());
                loader.load();
                view = loader.getController();
                view.setModel(translationsModel);

                if(PreferenceChangedEvent.class.isAssignableFrom(view.getClass())){
                    PreferenceChangedEvent event = (PreferenceChangedEvent) view;
                    event.registerListener(this);
                }

            } catch (IOException ignored) {

            }
    }

    @Override
    public void showPreferencesPopup(){
        loggerModel.addDebug("ui_start_popup_build");
        view.build();
        loggerModel.addDebug("ui_end_popup_build");
        view.show();
        loggerModel.addDebug("ui_popup_show");
    }

    @Override
    public void preferenceChange(PreferenceChangeEvent evt) {
        try {
            model.setPreference(evt.getKey(), evt.getNewValue());
        } catch (IOException e) {
            ErrorController.getInstance().showError(e.getMessage());
        }
        loggerModel.addInfo("ui_preferences_stored");
    }
}
