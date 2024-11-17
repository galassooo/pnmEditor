package ch.supsi.business.image;

import java.util.Arrays;
import java.util.Objects;

/**
 * Builder class for creating and configuring instances of images.
 * Implements {@link ImageBuilderInterface} to provide methods for setting image attributes,
 * performing validation, and supporting optional pixel conversion via an adapter.
 */
public class ImageBuilder implements ImageBuilderInterface{
    private long[][] pixels;
    private String filePath;
    private String magicNumber;
    private ImageAdapterInterface imageAdapter;

    @Override
    public long[][] getPixels() {
        return pixels;
    }

    @Override
    public String getFilePath() {
        return filePath;
    }

    @Override
    public String getMagicNumber() {
        return magicNumber;
    }


    /**
     * Sets the pixel data for the image.
     *
     * @param pixels a 2D array of pixels
     * @return the {@link ImageBuilder} instance for method chaining
     */
    public ImageBuilder withPixels(long[][] pixels) {
        this.pixels = pixels;
        return this;
    }

    /**
     * Sets the file path for the image.
     *
     * @param filePath the file path as a {@link String}
     * @return the {@link ImageBuilder} instance for method chaining
     */
    public ImageBuilder withFilePath(String filePath) {
        this.filePath = filePath;
        return this;
    }

    /**
     * Sets the magic number representing the image format.
     *
     * @param magicNumber the magic number as a {@link String}
     * @return the {@link ImageBuilder} instance for method chaining
     */
    public ImageBuilder withMagicNumber(String magicNumber) {
        this.magicNumber = magicNumber;
        return this;
    }

    /**
     * Sets an optional adapter for converting raw pixel data to ARGB format.
     *
     * @param imageAdapter the {@link ImageAdapterInterface} to use for pixel conversion
     * @return the {@link ImageBuilder} instance for method chaining
     */
    public ImageBuilder withImageAdapter(ImageAdapterInterface imageAdapter) {
        this.imageAdapter = imageAdapter;
        return this;
    }

    /**
     * Builds the image after validating the provided attributes.
     * If an adapter is provided, it converts the raw pixel data to ARGB format.
     *
     * @return the {@link ImageBuilder} instance
     * @throws IllegalStateException if required attributes (filePath or magicNumber) are missing
     */
    public ImageBuilder build() {
        // Validazione
        if (pixels == null) {
            pixels = new long[0][0];
        }
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalStateException("FilePath is required");
        }
        if (magicNumber == null || magicNumber.isEmpty()) {
            throw new IllegalStateException("MagicNumber is required");
        }

        if(imageAdapter!= null){
            pixels = imageAdapter.rawToArgb(pixels);
        }
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImageBuilder that)) return false;
        return Objects.deepEquals(getPixels(), that.getPixels()) && Objects.equals(getFilePath(), that.getFilePath()) && Objects.equals(getMagicNumber(), that.getMagicNumber()) && Objects.equals(imageAdapter, that.imageAdapter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.deepHashCode(getPixels()), getFilePath(), getMagicNumber(), imageAdapter);
    }
}