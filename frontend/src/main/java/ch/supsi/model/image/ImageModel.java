package ch.supsi.model.image;

import ch.supsi.application.image.ImageApplication;
import ch.supsi.application.image.ImageBusinessInterface;

import java.io.IOException;

public class ImageModel implements IImageModel{

    private static ImageModel myself;

    private static final ImageApplication backendController;


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

    public void readImage(String path) throws IOException, IllegalAccessException {
            backendController.read(path);
    }
    public  ImageBusinessInterface getImage(){
        return backendController.getImage();
    }

    public String getImageName(){
        return backendController.getImageName();
    }

    public void writeImage(String path) throws IOException, IllegalAccessException {
           backendController.persist(path);
    }
}
