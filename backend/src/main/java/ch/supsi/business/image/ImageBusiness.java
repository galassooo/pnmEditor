package ch.supsi.business.image;

import ch.supsi.application.image.WritableImage;
import ch.supsi.dataaccess.image.DataAccessFactory;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class ImageBusiness implements WritableImage {

    /* instance field */
    private long[][] argbPixels;
    private String filePath;
    private final String magicNumber;

    // Tutti i formati di immagine
    // (PNG, BMP, GIF, JPEG, TIFF, ICO, PPM, PGM, PBM etc...)
    // hanno un magicNumber che permette al software di riconoscerne
    // il tipo senza basarsi sull'estensione. Di conseguenza va incluso
    // come attributo di un immagine generica
    // P + valore nei PNM è semplicemente codificato in ascii e non in byte

    public ImageBusiness(ImageBuilderInterface builder) {
        this.argbPixels = builder.getPixels();
        this.filePath = builder.getFilePath();
        this.magicNumber = builder.getMagicNumber();
    }

    public static WritableImage read(String path) throws IOException, IllegalAccessException {
        ImageDataAccess dac = DataAccessFactory.getInstance(path);
        return dac.read(path);
    }

    @Override
    public void persist(String path) throws IOException, IllegalAccessException {
        ImageDataAccess dac = DataAccessFactory.getInstance(filePath);
        if (path != null)
            filePath = !filePath.equals(path) ? path : filePath;
        dac.write(this);
    }

    @Override
    public void export(String extension, String path) throws IOException, IllegalAccessException {
        ImageDataAccess dac = DataAccessFactory.getInstanceFromExtension(extension);
        String magicNumber = String.valueOf(DataAccessFactory.getDefaultMagicNumberFromExtension(extension));

        ImageBuilder exportedImage = new ImageBuilder()
                .withFilePath(!filePath.equals(path) ? path : filePath)
                .withMagicNumber(magicNumber)
                .withPixels(argbPixels)
                .build();

        dac.write(new ImageBusiness(exportedImage));
    }


    @Override
    public long[][] getPixels() {
        return argbPixels;
    }


    @Override
    public int getWidth() {
        //argbPixels sempre diverso da null perchè viene convertito in long[0][0]
        //dal builder in caso sia null
        return argbPixels.length > 0 ? argbPixels[0].length : 0;
    }


    @Override
    public int getHeight() {
        return argbPixels.length;
    }

    @Override
    public String getFilePath() {
        return filePath;
    }

    @Override
    public String getMagicNumber() {
        return magicNumber;
    }

    @Override
    public String getName() {
        File file = new File(filePath);
        return file.getName();
    }

    @Override
    public void setPixels(long[][] rotatedPixels) {
        argbPixels = rotatedPixels;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImageBusiness that)) return false;
        return Objects.deepEquals(argbPixels, that.argbPixels) && Objects.equals(getFilePath(), that.getFilePath()) && Objects.equals(getMagicNumber(), that.getMagicNumber());
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.deepHashCode(argbPixels), getFilePath(), getMagicNumber());
    }
}

