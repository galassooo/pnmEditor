package org.supsi.dispatcher;

import org.supsi.controller.about.AboutController;
import org.supsi.controller.about.IAboutController;
import org.supsi.controller.confirmation.ConfirmationController;
import org.supsi.controller.confirmation.IConfirmationController;
import org.supsi.controller.image.IImageController;
import org.supsi.controller.image.ImageController;
import org.supsi.controller.preferences.IPreferencesController;
import org.supsi.controller.preferences.PreferencesController;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import org.supsi.model.state.IStateModel;
import org.supsi.model.state.StateModel;
import java.io.IOException;
import java.util.function.Consumer;

public class MenuDispatcher {

    private static final IImageController imageController;
    private static final IPreferencesController preferencesController;
    private static final IAboutController aboutController;
    private static final IConfirmationController confirmationController;

    private final IStateModel stateModel = StateModel.getInstance();


    static{
        imageController = ImageController.getInstance();
        preferencesController = PreferencesController.getInstance();
        aboutController = AboutController.getInstance();
        confirmationController = ConfirmationController.getInstance();
    }

    @FXML
    public MenuItem saveMenuItem;

    @FXML
    public MenuItem saveAsMenuItem;



    @FXML
    private void initialize(){
        saveMenuItem.disableProperty().bind(stateModel.canSaveProperty().not());
        saveAsMenuItem.disableProperty().bind(stateModel.canSaveAsProperty().not());
    }


    public void save() throws IOException, IllegalAccessException {
        imageController.save();
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
        confirmationController.requestConfirm(new Consumer<Object>() {
            @Override
            public void accept(Object o) {
                System.out.println("uscita");
            }
        });
    }

    public void about(){
        aboutController.showPopup();
    }

}
