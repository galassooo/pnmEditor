package org.supsi.controller.image;

import org.supsi.controller.confirmation.ConfirmationController;
import org.supsi.controller.confirmation.IConfirmationController;
import org.supsi.controller.errors.ErrorController;
import org.supsi.controller.errors.IErrorController;
import org.supsi.view.image.ExportEvent;
import org.supsi.model.event.EventManager;
import org.supsi.model.event.EventSubscriber;
import org.supsi.model.state.IStateModel;
import org.supsi.model.state.StateModel;
import org.supsi.model.image.IImageModel;
import org.supsi.model.image.ImageModel;
import org.supsi.model.info.ILoggerModel;
import org.supsi.model.info.LoggerModel;
import org.supsi.view.fileSystem.FileSystemView;
import org.supsi.view.fileSystem.IFileSystemView;
import org.supsi.view.image.IImageView;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;

public class ImageController implements  IImageController {

    private static ImageController myself;
    private final IImageModel model;
    private final IErrorController errorController;
    private final ILoggerModel loggerModel;
    private final IConfirmationController confirmationController;
    private IImageView mainImageView;
    private Stage root;


    private ImageController() {
        model = ImageModel.getInstance();
        errorController = ErrorController.getInstance();
        loggerModel = LoggerModel.getInstance();
        confirmationController = ConfirmationController.getInstance();

        IStateModel stateModel = StateModel.getInstance();
        stateModel.refreshRequiredProperty().addListener((obs, old, newValue) -> {
            if (newValue) {
                mainImageView.update();
            }
        });
        EventSubscriber subscriber = EventManager.getSubscriber();
        subscriber.subscribe(ExportEvent.ExportRequested.class, this::onExportRequested );
    }

    public static ImageController getInstance(){
        if(myself==null){
            myself=new ImageController();
        }
        return myself;
    }

    @Override
    public void open() {
        confirmationController.requestConfirm((event) -> openImage());
    }



    @Override
    public void save() throws IOException, IllegalAccessException {
            model.writeImage(null);
            loggerModel.addInfo("ui_image_saved");
    }

    @Override
    public void saveAs() {
        IFileSystemView fsPopUp = new FileSystemView(root);
        File chosen = fsPopUp.askForDirectory();

        if(chosen == null){ //popup closed
            return;
        }
        try {
            model.writeImage(chosen.getPath());
        }catch(Exception e ){
            errorController.showError(e.getMessage());
        }
        loggerModel.addInfo("ui_image_saved");
    }

    @Override
    public void setImage(IImageView image) {
        this.mainImageView = image;
    }

    @Override
    public void setStage(Stage stage){
        this.root = stage;
    }

    private void onExportRequested(ExportEvent.ExportRequested event) {
        IFileSystemView fsPopUp = new FileSystemView(root);
        fsPopUp.setFileExtension(event.extension());
        File chosen = fsPopUp.askForDirectory();


        if(chosen == null){ //popup closed
            return;
        }
        try {
            model.export(event.extension(), chosen.getPath());
        }catch(Exception e ){
            errorController.showError(e.getMessage());
        }
        loggerModel.addInfo("ui_image_exported");
    }

    private void openImage(){

        IFileSystemView fsPopUp = new FileSystemView(root);
        File chosen = fsPopUp.askForFile();

        if(chosen == null){ //popup closed
            return;
        }

        try {
            model.readImage(chosen.getPath());
            loggerModel.addInfo("ui_image_loaded");
        }catch(Exception e ){
            errorController.showError(e.getMessage());
        }
    }
}
