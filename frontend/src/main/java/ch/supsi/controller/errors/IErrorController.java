package ch.supsi.controller.errors;

import javafx.stage.Stage;

public interface IErrorController {
    void setStage(Stage stage);
    void showError(String message);
}
