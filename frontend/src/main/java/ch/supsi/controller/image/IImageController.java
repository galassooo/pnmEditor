package ch.supsi.controller.image;

import ch.supsi.view.image.IImageView;
import javafx.stage.Stage;

public interface IImageController {


    void save();

    void saveAs();

    void open();

    void setImage(IImageView image);

    void setStage(Stage stage);
}
