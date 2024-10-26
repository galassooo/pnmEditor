package ch.supsi.view.preferences;

import ch.supsi.model.errors.IErrorModel;
import ch.supsi.model.preferences.IPreferencesModel;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.net.URL;

public class PreferencesView implements IPreferencesView {


    @FXML
    private BorderPane root;

    @FXML
    private Button saveButton;

    @FXML
    private Button closeButton;

    @FXML
    private ChoiceBox<String> choiceBox;

    private Stage myStage;

    private IPreferencesModel model;


    @FXML
    private void initialize() {
        closeButton.setOnAction(event -> myStage.close());

        myStage = new Stage();
        myStage.setScene(new Scene(root));
        myStage.setResizable(false);
    }

    public void setModel(IPreferencesModel model) {
        this.model = model;
    }

    @Override
    public void show() {
        myStage.show();
    }

    @Override
    public void build() {
        //ask model for available language
    }
}
