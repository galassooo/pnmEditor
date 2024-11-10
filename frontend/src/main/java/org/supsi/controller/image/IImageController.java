package org.supsi.controller.image;

import org.supsi.view.image.IExportEvent;
import org.supsi.view.image.IImageView;
import javafx.stage.Stage;

import java.io.IOException;

public interface IImageController {

    void save() throws IOException, IllegalAccessException;

    void saveAs();

    void open();

    void setImage(IImageView image);

    void setStage(Stage stage);

    void setExportEvent(IExportEvent eventGenerator);
}
