package ch.supsi.controller.image;

import ch.supsi.controller.errors.ErrorController;
import ch.supsi.controller.errors.IErrorController;
import ch.supsi.model.image.IImageModel;
import ch.supsi.model.image.ImageModel;
import ch.supsi.view.FileSystemView;
import ch.supsi.view.IFileSystemView;
import ch.supsi.view.image.IImageView;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImageController implements  IImageController{

    private IImageView mainImageView;

    private final IImageModel model = ImageModel.getInstance();
    private final IErrorController errorController = ErrorController.getInstance();

    private static ImageController myself;

    private Stage root;

    private List<MenuItem> controlledItems = new ArrayList<>();

    public static ImageController getInstance(){
        if(myself==null){
            myself=new ImageController();
        }
        return myself;
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
        controlledItems.forEach(item -> item.setDisable(false));
        mainImageView.update();
    }

    @Override
    public void addControlledItems(MenuItem... menuItems){
        controlledItems.addAll(Arrays.asList(menuItems));
    }
    @Override
    public void save() throws IOException, IllegalAccessException {
            model.writeImage(null);

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
