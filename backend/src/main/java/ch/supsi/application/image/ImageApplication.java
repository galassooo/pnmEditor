package ch.supsi.application.image;

import ch.supsi.business.image.ImageBusiness;
import ch.supsi.business.state.BusinessEditorState;
import ch.supsi.application.state.EditorStateManager;
import ch.supsi.dataaccess.image.DataAccessFactory;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * The {@link ImageApplication} class provides functionalities for managing image-related
 * operations in the application. It acts as an interface for loading, saving, and exporting
 * images, while maintaining the application's editor state.
 */
public class ImageApplication {

    private static ImageApplication myself;
    private final EditorStateManager stateManager;
    private WritableImage currentImage;

    /**
     * Private constructor
     */
    private ImageApplication() {
        stateManager = BusinessEditorState.getInstance();
    }


    /**
     * Returns the singleton instance of the {@code ImageApplication}.
     *
     * @return the singleton instance of the {@code ImageApplication}.
     */
    public static ImageApplication getInstance() {
        if (myself == null) {
            myself = new ImageApplication();
        }
        return myself;
    }

    /**
     * Reads an image from the given file path.
     *
     * @param path the file path of the image to load.
     * @return the loaded {@code ImageBusinessInterface} instance.
     * @throws IOException            if an I/O error occurs during reading.
     * @throws IllegalAccessException if the file reader cannot be accessed.
     */
    public WritableImage read(String path) throws IOException, IllegalAccessException {
        stateManager.onLoading();
        try {
            currentImage = ImageBusiness.read(path);
            stateManager.onImageLoaded();
        } catch (IllegalAccessException | IOException e) {
            if (currentImage == null) {
                stateManager.onLoadingError(); //First load attempt failed
            } else {
                stateManager.onImageLoaded(); //First image loaded but subsequent load failed
            }
            throw e; //Re-throw the exception
        }
        return currentImage;
    }

    /**
     * Saves the current image to the given file path.
     *
     * @param path the file path to save the image to.
     * @throws IOException            if an I/O error occurs during saving.
     * @throws IllegalAccessException if the file writer cannot be accessed.
     */
    public void persist(String path) throws IOException, IllegalAccessException {
        stateManager.onLoading();
        currentImage.persist(path);
        stateManager.onImageLoaded();
    }

    /**
     * Exports the current image to the specified path with the given file extension.
     *
     * @param extension the file extension for the export (e.g., "jpg", "png").
     * @param path      the file path to save the exported image.
     * @throws IOException            if an I/O error occurs during export.
     * @throws IllegalAccessException if the file writer cannot be accessed.
     */
    public void export(String extension, String path) throws IOException, IllegalAccessException {
        currentImage.export(extension, path);
    }


    /**
     * Retrieves a {@link List} of all supported file extensions for images.
     *
     * @return a {@link List} of supported file extensions.
     */
    public List<String> getAllSupportedExtension() {
        return DataAccessFactory.getSupportedExtensions();
    }

    /**
     * Returns the name of the currently loaded image file.
     *
     * @return an {@link Optional} containing the file name, or an empty {@code Optional} if no image is loaded.
     */
    public Optional<String> getImageName() {
        return Optional.ofNullable(currentImage != null ? currentImage.getName() : null);
    }

    /**
     * Returns the pixel data of the currently loaded image.
     *
     * @return an {@link Optional} containing a 2D array of pixel data, or an empty {@code Optional} if no image is loaded.
     */
    public Optional<long[][]> getImagePixels() {
        return Optional.ofNullable(currentImage != null ? currentImage.getPixels() : null);
    }

    /**
     * Returns the currently loaded image instance.
     *
     * @return an {@link Optional} containing the current {@code ImageBusinessInterface}, or an empty {@code Optional} if no image is loaded.
     */
    public Optional<WritableImage> getCurrentImage() {
        return Optional.ofNullable(currentImage);
    }
}
