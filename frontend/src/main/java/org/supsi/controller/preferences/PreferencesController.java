package org.supsi.controller.preferences;

import org.supsi.controller.errors.ErrorController;
import org.supsi.view.IView;
import org.supsi.view.preferences.PreferenceEvent;
import org.supsi.model.event.EventManager;
import org.supsi.model.event.EventSubscriber;
import org.supsi.model.info.ILoggerModel;
import org.supsi.model.info.LoggerModel;
import org.supsi.model.preferences.IPreferencesModel;
import org.supsi.model.preferences.PreferencesModel;
import org.supsi.model.translations.ITranslationsModel;
import org.supsi.model.translations.TranslationModel;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.net.URL;

/**
 * The {@code PreferencesController} class manages user preferences within the application.
 * It provides functionalities for displaying the preferences UI, handling preference changes,
 * and updating the underlying preferences model. This controller also integrates logging and
 * error reporting functionalities.
 */
public class PreferencesController implements IPreferencesController {

    private static PreferencesController mySelf;
    private IView<ITranslationsModel> view;
    private final IPreferencesModel model;
    private final ILoggerModel loggerModel;

    /**
     * Protected constructor to enforce the singleton pattern.
     * Initializes the preferences model, logger model, event subscriptions, and loads the
     * preferences popup view.
     */
    protected PreferencesController() {
        model = PreferencesModel.getInstance();
        loggerModel = LoggerModel.getInstance();

        EventSubscriber subscriber = EventManager.getSubscriber();
        subscriber.subscribe(PreferenceEvent.PreferenceChanged.class,
                this::preferenceChange);
        URL fxmlUrl = getClass().getResource("/layout/PreferencesPopup.fxml");
        if (fxmlUrl == null) {
            return;
        }

        try {
            ITranslationsModel translationsModel = TranslationModel.getInstance();
            FXMLLoader loader = new FXMLLoader(fxmlUrl, translationsModel.getUiBundle());
            loader.load();
            view = loader.getController();
            view.setModel(translationsModel);

        } catch (IOException ignored) {

        }
    }

    /**
     * Returns the singleton instance of the {@code PreferencesController}.
     *
     * @return the singleton instance of the {@code PreferencesController}.
     */
    public static PreferencesController getInstance() {
        if (mySelf == null) {
            mySelf = new PreferencesController();
        }
        return mySelf;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showPreferencesPopup(){
        loggerModel.addDebug("ui_start_popup_build");
        view.build();
        loggerModel.addDebug("ui_end_popup_build");
        view.show();
        loggerModel.addDebug("ui_popup_show");
    }

    /**
     * Handles a preference change event.
     * Updates the preferences model with the new value and logs the event.
     * If an error occurs, it is reported to the error controller.
     *
     * @param parentEvent the {@code PreferenceChanged} event containing the key and new value of the preference.
     */
    private void preferenceChange(PreferenceEvent.PreferenceChanged parentEvent) {
        try {
            model.setPreference(parentEvent.event().getKey(), parentEvent.event().getNewValue());
        } catch (IOException e) {
            ErrorController.getInstance().showError(e.getMessage());
        }
        loggerModel.addInfo("ui_preferences_stored");
    }
}
