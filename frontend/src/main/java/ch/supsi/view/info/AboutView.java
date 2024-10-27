package ch.supsi.view.info;

import ch.supsi.model.about.AboutModel;
import ch.supsi.model.about.IAboutModel;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class AboutView implements IAboutView {


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

    @FXML
    private void initialize(){
        myStage = new Stage();
        myStage.setScene(new Scene(root));
        myStage.setResizable(false);

        closeButton.setOnAction(event -> myStage.close());
    }

    @Override
    public void show() {
        myStage.show();
    }

    @Override
    public void build(){
        developer.setText(model.getDeveloper());
        version.setText(model.getVersion());
        date.setText(model.getDate());
    }

    @Override
    public void setModel(IAboutModel model) {
        this.model = model;
    }
}
