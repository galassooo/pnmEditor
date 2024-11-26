package org.supsi.view.info;

import javafx.stage.Modality;
import org.supsi.model.errors.IErrorModel;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.supsi.view.IView;
import java.net.URL;

/**
 * Represents an error popup dialog.
 * Displays an error message along with an associated icon and provides a button to close the dialog.
 * Implements the {@link IView} interface for consistent behavior across views.
 */
public class ErrorPopUp implements IView<IErrorModel> {

    @FXML
    private BorderPane root;

    @FXML
    private Label message;

    @FXML
    private ImageView image;

    @FXML
    private Button closeButton;

    private Stage myStage;

    private IErrorModel model;


    /**
     * Initializes the error popup.
     * Sets up the stage, configures the close button, and loads the error icon.
     * Logs an error if the icon cannot be loaded.
     */
    @FXML
    private void initialize() {
        closeButton.setOnAction(event -> myStage.close());

        myStage = new Stage();
        myStage.setScene(new Scene(root));
        myStage.setResizable(false);
        myStage.initModality(Modality.APPLICATION_MODAL);

        URL imageUrl = getClass().getResource("/images/icons/sadFile.png");
        if (imageUrl == null) {
            System.err.println("Error while loading sadFile image");
            return;
        }
        Image image = new Image(imageUrl.toExternalForm());
        this.image.setImage(image);


    }

    /**
     * Builds or updates the error popup with data from the associated model.
     * Sets the error message text in the popup.
     */
    @Override
    public void build(){
        message.setText(model.getMessage());
    }

    /**
     * Displays the error popup to the user.
     */
    @Override
    public void show(){
        myStage.show();
    }

    /**
     * Sets the model for the error popup.
     *
     * @param model the {@link IErrorModel} providing data for the popup
     */
    @Override
    public void setModel(IErrorModel model) {
        this.model = model;
    }
}
