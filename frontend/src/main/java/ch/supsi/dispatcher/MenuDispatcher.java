package ch.supsi.dispatcher;

import ch.supsi.controller.image.IImageController;
import ch.supsi.controller.image.ImageController;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;

import java.io.IOException;

public class MenuDispatcher implements ImageLoadedListener {

    private static final IImageController imageController;

    static{
        imageController = ImageController.getInstance();
    }

    @FXML
    public MenuItem saveMenuItem;

    @FXML
    public MenuItem saveAsMenuItem;

    @FXML
    private void initialize(){
        ImageController.getInstance().subscribe(this);
    }


    public void save() throws IOException, IllegalAccessException {
        imageController.save();
    }

    public void export() throws IOException, IllegalAccessException {
        // ???????
    }

    public void saveAs(){
        imageController.saveAs();
    }
    public void preferences(){
        //preferences ctrl
    }

    public void open(){
        imageController.open();
    }

    public void close(){
        //exit ctrl
    }

    public void about(){
        //about ctrl
    }

    //NON MI PIACE!
    @Override
    public void onImageLoaded(){
        saveAsMenuItem.setDisable(false);
        saveMenuItem.setDisable(false);
    }
}
