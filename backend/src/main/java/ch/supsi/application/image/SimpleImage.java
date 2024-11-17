package ch.supsi.application.image;

/**
 * Represents a simple image with basic properties and read-only operations.
 * This interface provides methods to access image characteristics such as dimensions,
 * pixel data, and file information without modifying the underlying image.
 */
public interface SimpleImage {

    /**
     * Gets the width of the image in pixels.
     *
     * @return the width of the image
     */
    int getWidth();

    /**
     * Gets the height of the image in pixels.
     *
     * @return the height of the image
     */
    int getHeight();

    /**
     * Gets the pixel data of the image as a 2D array of ARGB values.
     * Each pixel is represented as a long value in ARGB format.
     *
     * @return a 2D array containing the image's pixel data
     */
    long[][] getPixels();

    /**
     * Gets the file path of the image.
     *
     * @return the absolute path to the image file
     */
    String getFilePath();

    /**
     * Gets the filename of the image.
     *
     * @return the name of the image file
     */
    String getName();

    /**
     * Gets the magic number that identifies the image format.
     *
     * @return the magic number identifying the image format
     */
    String getMagicNumber();
}