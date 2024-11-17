package org.supsi.view.info;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Represents a confirmation popup dialog.
 * Provides a user interface with "Confirm" and "Cancel" buttons to determine whether the user wants to proceed.
 */
public class ConfirmPopup implements IConfirmPopup{

    @FXML
    private BorderPane root;

    @FXML
    private Button cancelButton;

    @FXML
    private Button confirmButton;

    private Stage myStage;

    private boolean wantsToContinue;

    /**
     * Initializes the confirmation popup.
     * Sets up the stage and defines the behavior for the "Confirm" and "Cancel" buttons.
     */
    @FXML
    void initialize() {
        myStage = new Stage();
        myStage.setScene(new Scene(root));
        myStage.setResizable(false);


        confirmButton.setOnAction(e -> {
            wantsToContinue = true;
            myStage.close();
        });

        cancelButton.setOnAction(e -> {
            wantsToContinue = false;
            myStage.close();
        });
    }

    /**
     * Displays the confirmation dialog and waits for the user's response.
     *
     * @return {@code true} if the user clicked "Confirm", {@code false} if the user clicked "Cancel"
     */
    @Override
    public boolean showConfirmationDialog() {
        myStage.showAndWait();
        return wantsToContinue;
    }
}
