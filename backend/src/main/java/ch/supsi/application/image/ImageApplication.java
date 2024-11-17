package ch.supsi.application.image;

import ch.supsi.business.image.ImageBusiness;
import ch.supsi.business.state.BusinessEditorState;
import ch.supsi.application.state.EditorStateManager;
import ch.supsi.dataaccess.image.DataAccessFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

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
            else stateManager.onImageLoaded(); //prima immagine già caricata ma seconda apertura fallita
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
    public Optional<String> getImageName(){
        return Optional.of(currentImage.getName()); //nullable
    }

    public Optional<long[][]> getImagePixels(){
        return Optional.of(currentImage.getPixels()); //nullable
    }

    public Optional<ImageBusinessInterface> getCurrentImage() {
        return Optional.of(currentImage); //nullable
    }

    public void export(String extension, String path) throws IOException, IllegalAccessException {
        currentImage.export(extension, path);
    }

}
