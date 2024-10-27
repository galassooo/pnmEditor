package ch.supsi.controller.image;

import ch.supsi.controller.errors.ErrorController;
import ch.supsi.controller.errors.IErrorController;
import ch.supsi.controller.filter.FilterController;
import ch.supsi.model.image.IImageModel;
import ch.supsi.model.image.ImageModel;
import ch.supsi.model.info.ILoggerModel;
import ch.supsi.model.info.LoggerModel;
import ch.supsi.view.fileSystem.FileSystemView;
import ch.supsi.view.fileSystem.IFileSystemView;
import ch.supsi.view.image.IImageView;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageController implements  IImageController, FiltersProcessedEvent {

    private IImageView mainImageView;

    private final IImageModel model = ImageModel.getInstance();
    private final IErrorController errorController = ErrorController.getInstance();
    private final ILoggerModel loggerModel = LoggerModel.getInstance();

    private final List<ImageLoadedListener> subscribers = new ArrayList<>();
    private static ImageController myself;

    private Stage root;


    public static ImageController getInstance(){
        if(myself==null){
            myself=new ImageController();
        }
        return myself;
    }

    private ImageController() {
        FilterController.subscribe(this);
    }

    @Override
    public void open() {
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
            return;
        }
        subscribers.forEach(ImageLoadedListener::onImageLoaded);
        mainImageView.update();
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

    @Override
    public void onPipelineProcessed() {
        mainImageView.update();
    }

    public void subscribe(ImageLoadedListener subscriber) {
        subscribers.add(subscriber);
    }
}
