package ch.supsi.application.image;

import ch.supsi.business.image.ImageBusiness;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;

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
    public List<String> getAllSupportedExtension(){
        return null;
    }

    /**
     * return the image name in the file system
     * @return the file name or null if no image was loaded
     */
    public @Nullable  String getImageName(){
        return currentImage == null ? null : currentImage.getName();
    }

    public long[][] getImagePixels(){
        return currentImage == null ? null : currentImage.getPixels();
    }

    public ImageBusinessInterface getCurrentImage() {
        return currentImage;
    }
}
