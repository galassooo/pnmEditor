package ch.supsi.controller.image;

import ch.supsi.controller.errors.ErrorController;
import ch.supsi.controller.errors.IErrorController;
import ch.supsi.model.image.IImageModel;
import ch.supsi.model.image.ImageModel;
import ch.supsi.view.FileSystemView;
import ch.supsi.view.IFileSystemView;
import ch.supsi.view.image.IImageView;
import javafx.stage.Stage;

import java.io.File;

public class ImageController implements  IImageController{

    private IImageView mainImageView;

    private final IImageModel model = ImageModel.getInstance();
    private final IErrorController errorController = ErrorController.getInstance();

    private static ImageController myself;

    private Stage root;

    public static ImageController getInstance(){
        if(myself==null){
            myself=new ImageController();
        }
        return myself;
    }


    @Override
    public void save() {

    }

    @Override
    public void saveAs() {

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
        }catch(Exception e ){
            errorController.showError(e.getMessage());
            return;
        }
        mainImageView.update();
    }

    @Override
    public void setImage(IImageView image) {
        this.mainImageView = image;
    }

    @Override
    public void setStage(Stage stage){
        this.root = stage;
    }
}
