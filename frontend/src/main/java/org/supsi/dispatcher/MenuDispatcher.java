package org.supsi.dispatcher;

import ch.supsi.application.state.StateApplication;
import ch.supsi.application.state.StateChangeListener;
import javafx.beans.property.SimpleBooleanProperty;
import org.supsi.controller.about.AboutController;
import org.supsi.controller.about.IAboutController;
import org.supsi.controller.image.IImageController;
import org.supsi.controller.image.ImageController;
import org.supsi.controller.preferences.IPreferencesController;
import org.supsi.controller.preferences.PreferencesController;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;

import java.io.IOException;

public class MenuDispatcher implements StateChangeListener {

    private static final IImageController imageController;
    private static final IPreferencesController preferencesController;
    private static final IAboutController aboutController;

    //ha doppia interfaccia che uso
    private static final StateApplication editorState = StateApplication.getInstance();

    private final SimpleBooleanProperty canSaveProperty = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty canSaveAsProperty = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty canExportProperty = new SimpleBooleanProperty(false);

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
    public MenuItem exportMenuItem;


    @FXML
    private void initialize(){
        editorState.registerStateListener(this);

        saveMenuItem.disableProperty().bind(canSaveProperty.not());
        saveAsMenuItem.disableProperty().bind(canSaveAsProperty.not());
        exportMenuItem.disableProperty().bind(canExportProperty.not());
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

    @Override
    public void onStateChanged() {
        canSaveProperty.set(editorState.canSave());
        canSaveAsProperty.set(editorState.canSaveAs());
        canExportProperty.set(editorState.canExport());
    }
}
