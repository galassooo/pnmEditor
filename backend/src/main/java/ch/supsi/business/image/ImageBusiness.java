package ch.supsi.business.image;

import ch.supsi.application.image.WritableImage;
import ch.supsi.dataaccess.image.DataAccessFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

/**
 * Represents an image with pixel data, file path, and format information.
 * Implements {@link WritableImage} to provide methods for retrieving data,
 * persisting, and exporting images.
 */
public class ImageBusiness implements WritableImage {

    private long[][] argbPixels;
    private String filePath;
    private final String magicNumber;

    /**
     * Constructs an {@link ImageBusiness} object using the specified {@link ImageBuilderInterface}.
     *
     * @param builder the {@link ImageBuilderInterface} containing the image attributes
     */
    public ImageBusiness(ImageBuilderInterface builder) {
        this.argbPixels = builder.getPixels();
        this.filePath = builder.getFilePath();
        this.magicNumber = builder.getMagicNumber();
    }

    /**
     * Reads an image from the specified file path.
     *
     * @param path the path of the image file to read
     * @return a {@link WritableImage} representing the image
     * @throws IOException            if an error occurs while reading the file
     * @throws IllegalAccessException if there is an issue accessing the data access component
     */
    public static WritableImage read(String path) throws IOException, IllegalAccessException {
        ImageDataAccess dac = DataAccessFactory.getInstance(path);
        return dac.read(path);
    }

    /**
     * Persists the current image to the file system at the specified path.
     *
     * @param path the path where the image should be saved
     * @throws IOException            if an error occurs while writing the file
     * @throws IllegalAccessException if there is an issue accessing the data access component
     */
    @Override
    public void persist(String path) throws IOException, IllegalAccessException {
        ImageDataAccess dac = DataAccessFactory.getInstance(filePath);
        if (path != null)
            filePath = !filePath.equals(path) ? path : filePath;
        dac.write(this);
    }

    /**
     * Exports the current image to a different format and saves it at the specified path.
     *
     * @param extension the desired file extension for the exported image
     * @param path      the path where the exported image should be saved
     * @throws IOException            if an error occurs while writing the file
     * @throws IllegalAccessException if there is an issue accessing the data access component
     */
    @Override
    public void export(String extension, String path) throws IOException, IllegalAccessException {
        ImageDataAccess dac = DataAccessFactory.getInstanceFromExtension(extension);
        String magicNumber = (DataAccessFactory.getDefaultMagicNumberFromExtension(extension)).get();

        ImageBuilder exportedImage = new ImageBuilder()
                .withFilePath(!filePath.equals(path) ? path : filePath)
                .withMagicNumber(magicNumber)
                .withPixels(argbPixels)
                .build();

        dac.write(new ImageBusiness(exportedImage));
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public long[][] getPixels() {
        return argbPixels;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public int getWidth() {
        return argbPixels.length > 0 ? argbPixels[0].length : 0;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public int getHeight() {
        return argbPixels.length;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public String getFilePath() {
        return filePath;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public String getMagicNumber() {
        return magicNumber;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public String getName() {
        File file = new File(filePath);
        return file.getName();
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void setPixels(long[][] rotatedPixels) {
        argbPixels = rotatedPixels;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImageBusiness that)) return false;
        return Objects.deepEquals(argbPixels, that.argbPixels) &&
                Objects.equals(getFilePath(), that.getFilePath()) &&
                Objects.equals(getMagicNumber(), that.getMagicNumber());
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.deepHashCode(argbPixels), getFilePath(), getMagicNumber());
    }
}
