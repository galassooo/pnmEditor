package ch.supsi.business.image;

/**
 * Interface for handling image pixel format conversions.
 * Provides methods to convert between raw image formats and ARGB color space.
 */
public interface ImageAdapterInterface {

    /**
     * Converts raw image pixel data to ARGB format.
     *
     * @param rawImage the original pixel data in raw format
     * @return the pixel data converted to ARGB format
     */
    long[][] argbToRaw(long[][] rawImage);

    /**
     * Converts ARGB pixel data back to the raw format.
     *
     * @param rawImage the pixel data in ARGB format
     * @return the pixel data converted back to raw format
     */
    long[][] rawToArgb(long[][] rawImage);
}