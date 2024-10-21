package ch.supsi.application.Image;

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

    public ImageBusinessInterface read(String path) throws IOException {
        currentImage =  ImageBusiness.read(path);
        return currentImage;
    }

    public ImageBusinessInterface persist(String path) throws IOException {
        return currentImage.persist(path);

    }

}
