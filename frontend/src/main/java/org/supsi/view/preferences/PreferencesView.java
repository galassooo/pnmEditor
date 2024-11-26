package org.supsi.view.preferences;

import javafx.stage.Modality;
import org.supsi.model.event.EventManager;
import org.supsi.model.event.EventPublisher;
import org.supsi.model.preferences.IPreferencesModel;
import org.supsi.model.preferences.PreferencesModel;
import org.supsi.model.translations.ITranslationsModel;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.supsi.view.IView;

import java.util.HashMap;
import java.util.Map;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.Preferences;

/**
 * Represents the Preferences View in the application.
 * This view allows users to update language preferences and toggle various debug and logging options.
 * It interacts with the {@link ITranslationsModel} for translations and the {@link IPreferencesModel} for preference management.
 */
public class PreferencesView implements IView<ITranslationsModel> {

    @FXML
    private BorderPane preferencesPopupRoot;

    @FXML
    private Button preferencesPopupSave;

    @FXML
    private Button preferencesPopupClose;

    @FXML
    private ChoiceBox<String> choiceBox;

    @FXML
    private CheckBox debugCB;

    @FXML
    private CheckBox infoCB;

    @FXML
    private CheckBox errorCB;

    @FXML
    private CheckBox warningCB;

    private Stage myStage;

    private ITranslationsModel model;
    private final IPreferencesModel preferencesModel;
    private final Map<String, String> labelsAndCodesMap;
    private final EventPublisher publisher;

    /** Constructs a new PreferencesView and initializes dependencies. */
    public PreferencesView() {
        preferencesModel = PreferencesModel.getInstance();
        publisher = EventManager.getPublisher();
        labelsAndCodesMap = new HashMap<>();
    }

    /**
     * Initializes the preferences view.
     * Sets up event handlers for buttons and loads the current preferences.
     */
    @FXML
    private void initialize() {
        preferencesPopupClose.setOnAction(event -> myStage.close());

        myStage = new Stage();
        myStage.setScene(new Scene(preferencesPopupRoot));
        myStage.initModality(Modality.APPLICATION_MODAL);
        myStage.setResizable(false);

        debugCB.setSelected(Boolean.parseBoolean(preferencesModel.getPreference("show-debug").orElse("N/A").toString()));
        infoCB.setSelected(Boolean.parseBoolean(preferencesModel.getPreference("show-info").orElse("N/A").toString()));
        errorCB.setSelected(Boolean.parseBoolean(preferencesModel.getPreference("show-error").orElse("N/A").toString()));
        warningCB.setSelected(Boolean.parseBoolean(preferencesModel.getPreference("show-warning").orElse("N/A").toString()));

        preferencesPopupSave.setOnAction(event -> {
            if(choiceBox.getValue()!=null){
                Preferences dummyPreferences = Preferences.userRoot().node("dummy");
                PreferenceChangeEvent pEvent = new PreferenceChangeEvent(dummyPreferences, "language-tag",
                        labelsAndCodesMap.get(choiceBox.getValue()));

                publisher.publish(new PreferenceEvent.PreferenceChanged(pEvent));
            }

            notifyPreferenceChange("show-debug", debugCB );
            notifyPreferenceChange("show-info", infoCB );
            notifyPreferenceChange("show-warning", warningCB );
            notifyPreferenceChange("show-error", errorCB );

            myStage.close();
        });


    }

    /**
     * Sets the translations model for the view.
     *
     * @param model the translations model to be used
     */
    @Override
    public void setModel(ITranslationsModel model) {
        this.model = model;
    }

    /**
     * Displays the preferences view to the user.
     */
    @Override
    public void show() {
        myStage.show();
    }

    /**
     * Builds or updates the preferences view based on the current model data.
     * Populates the language choice box with supported languages.
     */
    @Override
    public void build() {
        choiceBox.getItems().removeAll(choiceBox.getItems());
        model.getSupportedLanguages().forEach(languageTag -> {
            String translation = model.translate(languageTag);
            labelsAndCodesMap.put(translation, languageTag);

            choiceBox.getItems().add(translation);
        });
    }

    /**
     * Publishes a preference change event for the specified preference and checkbox state.
     *
     * @param tag the preference key
     * @param box the checkbox controlling the preference
     */
    private void notifyPreferenceChange(String tag, CheckBox box){
        Preferences dummyPreferences = Preferences.userRoot().node("dummy");
        PreferenceChangeEvent pEvent = new PreferenceChangeEvent(dummyPreferences, tag, String.valueOf(box.isSelected()));

        publisher.publish(new PreferenceEvent.PreferenceChanged(pEvent));
    }
}
