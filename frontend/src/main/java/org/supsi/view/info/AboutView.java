package org.supsi.view.info;

import org.supsi.model.about.IAboutModel;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.supsi.view.IView;

/**
 * Represents the "About" view in the application.
 * Displays information about the application such as the developer, version, and release date.
 */
public class AboutView implements IView<IAboutModel> {

    @FXML
    private BorderPane root;

    @FXML
    private Label date;

    @FXML
    private Label version;

    @FXML
    private Label developer;

    @FXML
    private Button closeButton;

    private Stage myStage;

    private IAboutModel model;

    /**
     * Initializes the "About" view.
     * Sets up the stage and defines the behavior for the close button.
     */
    @FXML
    private void initialize(){
        myStage = new Stage();
        myStage.setScene(new Scene(root));
        myStage.setResizable(false);

        closeButton.setOnAction(event -> myStage.close());
    }

    /**
     * Displays the "About" view to the user.
     */
    @Override
    public void show() {
        myStage.show();
    }

    /**
     * Builds or updates the "About" view with data from the associated model.
     * Sets the text of labels to the application's developer, version, and release date.
     */
    @Override
    public void build(){
        developer.setText(model.getDeveloper());
        version.setText(model.getVersion());
        date.setText(model.getDate());
    }

    /**
     * Sets the model for the "About" view.
     *
     * @param model the {@link IAboutModel} providing data for the view
     */
    @Override
    public void setModel(IAboutModel model) {
        this.model = model;
    }
}
