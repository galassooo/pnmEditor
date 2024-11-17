package org.supsi.model.image;

import ch.supsi.application.image.ImageApplication;

import java.io.IOException;
import java.util.List;

/**
 * Represents the model for managing image data and operations.
 * Acts as a bridge between the view and the backend controller for image-related functionality.
 */
public class ImageModel implements IImageModel {

    private static ImageModel myself;
    private final ImageApplication backendController;

    /**
     * Constructs a new {@code ImageModel} instance and initializes the backend controller.
     */
    protected ImageModel() {
        backendController = ImageApplication.getInstance();
    }

    /**
     * Retrieves the singleton instance of this class.
     *
     * @return the singleton instance of {@code ImageModel}
     */
    public static ImageModel getInstance() {
        if (myself == null) {
            myself = new ImageModel();
        }
        return myself;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void readImage(String path) throws IOException, IllegalAccessException {
        backendController.read(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getImageName() {
        return backendController.getImageName().orElse("N/A");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long[][] getImagePixels() {
        return backendController.getImagePixels().orElse(new long[0][]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeImage(String path) throws IOException, IllegalAccessException {
        backendController.persist(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getSupportedExtensions() {
        return backendController.getAllSupportedExtension();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void export(String extension, String path) throws IOException, IllegalAccessException {
        backendController.export(extension, path);
    }
}
