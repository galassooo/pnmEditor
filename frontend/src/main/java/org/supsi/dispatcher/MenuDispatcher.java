package org.supsi.dispatcher;

import javafx.stage.Stage;
import org.supsi.controller.about.AboutController;
import org.supsi.controller.about.IAboutController;
import org.supsi.controller.exit.ExitController;
import org.supsi.controller.exit.IExitController;
import org.supsi.controller.image.IImageController;
import org.supsi.controller.image.ImageController;
import org.supsi.controller.preferences.IPreferencesController;
import org.supsi.controller.preferences.PreferencesController;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import org.supsi.model.state.IStateModel;
import org.supsi.model.state.StateModel;

/**
 * Manages the dispatching of menu actions to the appropriate controllers in the application.
 */
public class MenuDispatcher {

    private final IImageController imageController;
    private final IPreferencesController preferencesController;
    private final IAboutController aboutController;
    private final IExitController exitController;
    private final IStateModel stateModel;

    @FXML
    private MenuItem saveMenuItem;

    @FXML
    private MenuItem saveAsMenuItem;

    private Stage stage;

    public MenuDispatcher(){
        stateModel = StateModel.getInstance();
        imageController = ImageController.getInstance();
        preferencesController = PreferencesController.getInstance();
        aboutController = AboutController.getInstance();
        exitController = ExitController.getInstance();
    }

    /**
     * Initializes the menu dispatcher by binding menu items to state properties.
     */
    @FXML
    void initialize(){
        saveMenuItem.disableProperty().bind(stateModel.canSaveProperty().not());
        saveAsMenuItem.disableProperty().bind(stateModel.canSaveAsProperty().not());
    }

    /**
     * Sets the primary stage of the application.
     *
     * @param stage the primary stage
     */
    public void setStage(Stage stage){
        this.stage = stage;
    }

    /**
     * Handles the "Save" menu action.
     *
     */
    public void save() {
        imageController.save();
    }

    /**
     * Handles the "Save As" menu action.
     */
    public void saveAs(){
        imageController.saveAs();
    }

    /**
     * Handles the "Preferences" menu action, opening the preferences popup.
     */
    public void preferences(){
        preferencesController.showPreferencesPopup();
    }

    /**
     * Handles the "Open" menu action, opening an image file.
     */
    public void open(){
        imageController.open();
    }

    /**
     * Handles the "Close" menu action, closing the application.
     */
    public void close(){
        exitController.handleExit(stage);
    }

    /**
     * Handles the "About" menu action, displaying the about popup.
     */
    public void about(){
        aboutController.showPopup();
    }

}
