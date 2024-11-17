package org.supsi.model.image;

import java.io.IOException;
import java.util.List;

/**
 * Defines the behavior of an image model for managing image data and operations.
 * Provides methods for reading, writing, and exporting images, as well as retrieving image metadata.
 */
public interface IImageModel {

    /**
     * Reads an image from the specified file path.
     *
     * @param path the path of the image file to read
     * @throws IOException           if there is an error reading the file
     * @throws IllegalAccessException if access to the file is denied
     */
    void readImage(String path) throws IOException, IllegalAccessException;

    /**
     * Writes the current image to the specified file path.
     *
     * @param path the path where the image will be saved
     * @throws IOException           if there is an error writing the file
     * @throws IllegalAccessException if access to the file writer is inaccessible
     */
    void writeImage(String path) throws IOException, IllegalAccessException;

    /**
     * Retrieves the name of the current image file.
     *
     * @return a {@link String} representing the name of the image file
     */
    String getImageName();

    /**
     * Retrieves the pixel data of the current image.
     *
     * @return a 2D array of {@code long} values representing the ARGB pixels of the image
     */
    long[][] getImagePixels();

    /**
     * Retrieves the list of supported file extensions for images.
     *
     * @return a {@link List} of {@link String} containing the supported file extensions
     */
    List<String> getSupportedExtensions();

    /**
     * Exports the current image to the specified file path with the given file extension.
     *
     * @param extension the file extension to use for the exported image
     * @param path      the path where the image will be exported
     * @throws IOException           if there is an error exporting the file
     * @throws IllegalAccessException if access to the file writer is inaccessible
     */
    void export(String extension, String path) throws IOException, IllegalAccessException;
}
