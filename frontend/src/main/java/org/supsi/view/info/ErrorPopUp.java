package org.supsi.view.info;

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


    @FXML
    private void initialize() {
        closeButton.setOnAction(event -> myStage.close());

        myStage = new Stage();
        myStage.setScene(new Scene(root));
        myStage.setResizable(false);

        URL imageUrl = getClass().getResource("/images/icons/sadFile.png");
        if (imageUrl == null) {
            System.err.println("Error while loading sadFile image");
            return;
        }
        Image image = new Image(imageUrl.toExternalForm());
        this.image.setImage(image);


    }

    @Override
    public void build(){
        message.setText(model.getMessage());
    }

    @Override
    public void show(){
        myStage.show();
    }

    @Override
    public void setModel(IErrorModel model) {
        this.model = model;
    }
}
