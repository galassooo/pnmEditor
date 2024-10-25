package ch.supsi.business.image;

import ch.supsi.application.image.ImageBusinessInterface;
import ch.supsi.business.strategy.ConvertStrategy;
import ch.supsi.dataaccess.image.DataAccessFactory;
import ch.supsi.dataaccess.image.PGMDataAccess;

import java.io.File;
import java.io.IOException;

public class ImageBusiness implements ImageBusinessInterface {

    /* instance field */
    private long[][] argbPixels;
    private String filePath;

    // Tutti i formati di immagine
    // (PNG, BMP, GIF, JPEG, TIFF, ICO, PPM, PGM, PBM etc...)
    // hanno un magicNumber che permette al software di riconoscerne
    // il tipo senza basarsi sull'estensione. Di conseguenza va incluso
    // come attributo di un immagine generica
    // P + valore nei PNM è semplicemente codificato in ascii e non in byte
    private final String magicNumber;

    public ImageBusiness(long[][] original, String path, String magicNumber, ConvertStrategy strategy) {
        this.argbPixels = createArgbMatrix(original, strategy);
        filePath = path;

        this.magicNumber = magicNumber;
    }

    @Override
    public long[][] getPixels() {
        return argbPixels;
    }


    @Override
    public void setPixels(long[][] rotatedPixels) {
        argbPixels = rotatedPixels;
    }

    @Override
    public long[][] returnOriginalMatrix(ConvertStrategy strategy) {
        int height = argbPixels.length;
        if (argbPixels.length == 0) {
            return new long[0][0];
        }

        int width = argbPixels[0].length;

        long[][] pixels = new long[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x ++) {
                //estrae i valori originali dei canali
                pixels[y][x] = strategy.ArgbToOriginal(this.argbPixels[y][x]);
            }
        }

        return pixels;
    }
    @Override
    public ImageBusinessInterface persist(String path) throws IOException, IllegalAccessException {
        ImageDataAccess dac = DataAccessFactory.getInstance(filePath);
        if(path!= null)
            filePath = !filePath.equals(path) ? path : filePath;
        return dac.write(this);

    }

    public static ImageBusinessInterface read(String path) throws IOException, IllegalAccessException {
        ImageDataAccess dac = DataAccessFactory.getInstance(path);
        return dac.read(path);
    }

    @Override
    public int getWidth() {
        //argbPixels sempre diverso da null perchè viene convertito in long[0][0]
        //dal costruttore in caso sia null
        return  argbPixels.length > 0  ? argbPixels[0].length : 0;
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

    private long[][] createArgbMatrix(long[][] original, ConvertStrategy strategy) {
        if(original == null)
            return new long[0][0];
        int height = original.length;
        if (height == 0) {
            return new long[0][0];
        }
        int width = original[0].length;

        long[][] argbMatrix = new long[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                argbMatrix[y][x] = strategy.toArgb(original[y][x]);
            }
        }
        return argbMatrix;
    }
    @Override
    public String getName(){
        File file = new File(filePath);
        return  file.getName();
    }
}

