package ch.supsi.business.image;

import ch.supsi.application.image.WritableImage;
import java.io.IOException;

/**
 * Interface defining the operations for accessing image data.
 * Provides methods to read and write image files in the application.
 */
public interface ImageDataAccess {

    /**
     * Reads an image file from the specified path and processes it into a {@link WritableImage}.
     *
     * @param path the path of the image file to read
     * @return a {@link WritableImage} object representing the processed image
     * @throws IOException if an error occurs while reading the file
     */
    WritableImage read(String path) throws IOException;

    /**
     * Writes a {@link WritableImage} to a file at the specified path.
     *
     * @param image the {@link WritableImage} object to be written to the file
     * @return the {@link WritableImage} object that was written
     * @throws IOException if an error occurs while writing the file
     */
    WritableImage write(WritableImage image) throws IOException;
}
