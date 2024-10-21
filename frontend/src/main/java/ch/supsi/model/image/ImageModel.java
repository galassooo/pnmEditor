package ch.supsi.model.image;

import ch.supsi.application.Image.ImageApplication;
import ch.supsi.application.Image.ImageBusinessInterface;

import java.io.IOException;

public class ImageModel implements IImageModel{

    private static ImageModel myself;

    private static final ImageApplication backendController;

    private ImageBusinessInterface imageBusiness;

    static {
        backendController = ImageApplication.getInstance();
    }

    public static ImageModel getInstance(){
        if(myself==null){
            myself=new ImageModel();
        }
        return myself;
    }

    protected ImageModel() {
    }

    public void readImage(String path) throws IOException {
        imageBusiness = backendController.read(path);
    }
    public  ImageBusinessInterface getImage(){
        return imageBusiness;
    }

}
