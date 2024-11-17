package org.supsi.view.preferences;

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

public class PreferencesView implements IView<ITranslationsModel> {

    @FXML
    private BorderPane root;

    @FXML
    private Button saveButton;

    @FXML
    private Button closeButton;

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


    private PreferencesView() {
        preferencesModel = PreferencesModel.getInstance();
        publisher = EventManager.getPublisher();
        labelsAndCodesMap = new HashMap<>();
    }

    @FXML
    private void initialize() {
        closeButton.setOnAction(event -> myStage.close());

        myStage = new Stage();
        myStage.setScene(new Scene(root));
        myStage.setResizable(false);

        debugCB.setSelected(Boolean.parseBoolean(preferencesModel.getPreference("show-debug").orElse("N/A").toString()));
        infoCB.setSelected(Boolean.parseBoolean(preferencesModel.getPreference("show-info").orElse("N/A").toString()));
        errorCB.setSelected(Boolean.parseBoolean(preferencesModel.getPreference("show-error").orElse("N/A").toString()));
        warningCB.setSelected(Boolean.parseBoolean(preferencesModel.getPreference("show-warning").orElse("N/A").toString()));

        saveButton.setOnAction(event -> {
            if(choiceBox.getValue()!=null){
                Preferences dummyPreferences = Preferences.userRoot().node("dummy");
                PreferenceChangeEvent pEvent = new PreferenceChangeEvent(dummyPreferences, "language-tag",
                        labelsAndCodesMap.get(choiceBox.getValue()));

                publisher.publish(new PreferenceEvent.PreferenceChanged(pEvent));
            }


            notifyPref("show-debug", debugCB );
            notifyPref("show-info", infoCB );
            notifyPref("show-warning", warningCB );
            notifyPref("show-error", errorCB );


            myStage.close();
        });


    }

    public void setModel(ITranslationsModel model) {
        this.model = model;
    }

    @Override
    public void show() {
        myStage.show();
    }

    @Override
    public void build() {
        choiceBox.getItems().removeAll(choiceBox.getItems());
        model.getSupportedLanguages().forEach(languageTag -> {
            String translation = model.translate(languageTag);
            labelsAndCodesMap.put(translation, languageTag);

            choiceBox.getItems().add(translation);
        });
    }

    private void notifyPref(String tag, CheckBox box){
        Preferences dummyPreferences = Preferences.userRoot().node("dummy");
        PreferenceChangeEvent pEvent = new PreferenceChangeEvent(dummyPreferences, tag, String.valueOf(box.isSelected()));

        publisher.publish(new PreferenceEvent.PreferenceChanged(pEvent));
    }
}
