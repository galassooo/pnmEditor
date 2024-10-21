package ch.supsi.model.image;

import ch.supsi.application.image.ImageApplication;
import ch.supsi.application.image.ImageBusinessInterface;

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
        try {
            imageBusiness = backendController.read(path);
        } catch (IllegalAccessException e) {
            e.printStackTrace(); //to be changed
        }
    }
    public  ImageBusinessInterface getImage(){
        return imageBusiness;
    }

}
