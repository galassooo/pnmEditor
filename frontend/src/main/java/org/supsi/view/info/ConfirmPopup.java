package org.supsi.view.info;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ConfirmPopup implements IConfirmPopup{

    @FXML
    private BorderPane root;

    @FXML
    private Button cancelButton;

    @FXML
    private Button confirmButton;

    private Stage myStage;

    private boolean wantsToContinue;

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

    @Override
    public boolean showConfirmationDialog() {
        myStage.showAndWait();
        return wantsToContinue;
    }
}
