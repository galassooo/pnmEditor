package ch.supsi.application.image;

import ch.supsi.business.image.ImageBusiness;
import ch.supsi.business.state.BusinessEditorState;
import ch.supsi.application.state.EditorStateManager;
import ch.supsi.dataaccess.image.DataAccessFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;

public class ImageApplication {

    private static ImageApplication myself;
    private final EditorStateManager stateManager = BusinessEditorState.getInstance();

    public static ImageApplication getInstance(){
        if(myself == null){
            myself = new ImageApplication();
        }
        return myself;
    }

    private ImageApplication(){}

    private ImageBusinessInterface currentImage;

    public ImageBusinessInterface read(String path) throws IOException, IllegalAccessException {
        stateManager.onLoading();
        try {
            currentImage = ImageBusiness.read(path);
            stateManager.onImageLoaded();
        }catch (IllegalAccessException | IOException e){
            if(currentImage == null) stateManager.onLoadingError(); //first try caricamento
            else stateManager.onImageLoaded(); //immagine caricata ma seconda apertura fallita
            throw e; //re-throw
        }
        return currentImage;
    }

    public void persist(String path) throws IOException, IllegalAccessException {
        stateManager.onLoading();
        currentImage.persist(path);
        stateManager.onImageLoaded();
    }
    public List<String> getAllSupportedExtension(){
        return DataAccessFactory.getSupportedExtensions();
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

    public void export(String extension, String path) throws IOException, IllegalAccessException {
        currentImage.export(extension, path);
    }

}
