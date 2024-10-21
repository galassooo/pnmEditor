package ch.supsi.application.image;

import ch.supsi.business.image.ImageBusiness;

import java.io.IOException;

public class ImageApplication {

    private static ImageApplication myself;

    public static ImageApplication getInstance(){
        if(myself == null){
            myself = new ImageApplication();
        }
        return myself;
    }

    private ImageApplication(){}

    private ImageBusinessInterface currentImage;

    public ImageBusinessInterface read(String path) throws IOException, IllegalAccessException {
        currentImage =  ImageBusiness.read(path);
        return currentImage;
    }

    public ImageBusinessInterface persist(String path) throws IOException, IllegalAccessException {
        return currentImage.persist(path);

    }

}
