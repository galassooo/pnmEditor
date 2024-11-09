package org.supsi.dispatcher;

import org.supsi.controller.about.AboutController;
import org.supsi.controller.about.IAboutController;
import org.supsi.controller.image.IImageController;
import org.supsi.controller.image.ImageController;
import org.supsi.controller.image.ImageLoadedListener;
import org.supsi.controller.preferences.IPreferencesController;
import org.supsi.controller.preferences.PreferencesController;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;

import java.io.IOException;

public class MenuDispatcher implements ImageLoadedListener {

    private static final IImageController imageController;
    private static final IPreferencesController preferencesController;
    private static final IAboutController aboutController;

    static{
        imageController = ImageController.getInstance();
        preferencesController = PreferencesController.getInstance();
        aboutController = AboutController.getInstance();
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
        preferencesController.showPreferencesPopup();
    }

    public void open(){
        imageController.open();
    }

    public void close(){
        //exit ctrl
    }

    public void about(){
        aboutController.showPopup();
    }

    //NON MI PIACE!
    @Override
    public void onImageLoaded(){
        saveAsMenuItem.setDisable(false);
        saveMenuItem.setDisable(false);
    }
}
