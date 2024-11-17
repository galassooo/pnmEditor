package ch.supsi.application.image;

import java.io.IOException;

/**
 * Extends {@link SimpleImage} to provide write operations on images.
 * This interface includes methods for saving and exporting images in different formats,
 * as well as modifying pixel data.
 */
public interface WritableImage extends SimpleImage {
    /**
     * Persists the image to the specified path
     *
     * @param path the path where the image should be saved
     * @throws IOException if there is an error writing the file
     * @throws IllegalAccessException if the writer cannot be accessed
     */
    void persist(String path) throws IOException, IllegalAccessException;

    /**
     * Exports the image to a different format specified by the extension.
     *
     * @param extension the target format extension (e.g., "pbm", "pgm", "ppm")
     * @param path the path where the exported image should be saved
     * @throws IOException if there is an error writing the file
     * @throws IllegalAccessException if the writer cannot be accessed
     */
    void export(String extension, String path) throws IOException, IllegalAccessException;

    /**
     * Updates the image's pixel data with new values.
     *
     * @param rotatedPixels the new pixel data to replace the existing pixels
     */
    void setPixels(long[][] rotatedPixels);
}