package ch.supsi.business.strategy;

/**
 * Interface for defining color conversion strategies.
 * Provides methods to convert between different channel formats and ARGB.
 */
public interface ConvertStrategy {
    /**
     * Converts a pixel value from its original format to ARGB.
     *
     * @param pixel the original pixel value
     * @return the pixel value in ARGB format
     */
    long toArgb(long pixel);

    /**
     * Converts a pixel value from ARGB back to its original format.
     *
     * @param pixel the pixel value in ARGB format
     * @return the pixel value in the original format
     */
    long ArgbToOriginal(long pixel);
}