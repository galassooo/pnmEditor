package ch.supsi.business.image;

/**
 * Interface defining the basic properties needed to build an image.
 * This interface specifies the minimal set of attributes required to construct
 * a valid image object in the system.
 */
public interface ImageBuilderInterface {
    /**
     * Gets the pixel data for the image being built.
     *
     * @return a 2D array of pixel values
     */
    long[][] getPixels();

    /**
     * Gets the file path where the image will be stored.
     *
     * @return the path to the image file
     */
    String getFilePath();

    /**
     * Gets the magic number identifying the image format.
     *
     * @return the magic number string
     */
    String getMagicNumber();
}