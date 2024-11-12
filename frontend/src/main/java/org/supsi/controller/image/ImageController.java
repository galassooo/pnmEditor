package org.supsi.controller.image;

import org.supsi.controller.confirmation.ConfirmationController;
import org.supsi.controller.confirmation.IConfirmationController;
import org.supsi.controller.errors.ErrorController;
import org.supsi.controller.errors.IErrorController;
import org.supsi.model.state.IStateModel;
import org.supsi.model.state.StateModel;
import org.supsi.model.image.IImageModel;
import org.supsi.model.image.ImageModel;
import org.supsi.model.info.ILoggerModel;
import org.supsi.model.info.LoggerModel;
import org.supsi.view.fileSystem.FileSystemView;
import org.supsi.view.fileSystem.IFileSystemView;
import org.supsi.view.image.ExportEventListener;
import org.supsi.view.image.IExportEvent;
import org.supsi.view.image.IImageView;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;

public class ImageController implements  IImageController, ExportEventListener {

    private IImageView mainImageView;

    private final IImageModel model = ImageModel.getInstance();
    private final IErrorController errorController = ErrorController.getInstance();
    private final ILoggerModel loggerModel = LoggerModel.getInstance();
    private final IStateModel stateModel = StateModel.getInstance();
    private final IConfirmationController confirmationController = ConfirmationController.getInstance();

    private static ImageController myself;


    private Stage root;


    public static ImageController getInstance(){
        if(myself==null){
            myself=new ImageController();
        }
        return myself;
    }

    private ImageController() {
        stateModel.refreshRequiredProperty().addListener((obs, old, newValue) -> {
            if (newValue) {
                mainImageView.update();
            }
        });

    }

    @Override
    public void open() {
        confirmationController.requestConfirm((event) -> openImage());
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

    //terribile, ma sono obbligata a farlo in quanto il menu export è
    //parte dell'interfaccia di base dell'applicazione e quindi caricata dal main,
    @Override
    public void setExportEvent(IExportEvent eventGenerator) {
        eventGenerator.registerListener(this);
    }

    @Override
    public void setImage(IImageView image) {
        this.mainImageView = image;
    }

    @Override
    public void setStage(Stage stage){
        this.root = stage;
    }

    @Override
    public void onExportRequested(String extension) {
        IFileSystemView fsPopUp = new FileSystemView(root);
        fsPopUp.setFileExtension(extension);
        File chosen = fsPopUp.askForDirectory();


        if(chosen == null){ //popup closed
            return;
        }
        try {
            model.export(extension, chosen.getPath());
        }catch(Exception e ){
            errorController.showError(e.getMessage());
        }
        loggerModel.addInfo("ui_image_exported");
    }
}
