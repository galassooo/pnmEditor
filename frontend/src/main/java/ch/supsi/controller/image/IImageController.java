package ch.supsi.controller.image;

import ch.supsi.view.image.IImageView;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

import java.io.IOException;

public interface IImageController {


    void addControlledItems(MenuItem... menuItems);

    void save() throws IOException, IllegalAccessException;

    void saveAs();

    void open();

    void setImage(IImageView image);

    void setStage(Stage stage);
}
