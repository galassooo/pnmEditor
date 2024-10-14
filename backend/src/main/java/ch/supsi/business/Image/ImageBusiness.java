package ch.supsi.business.Image;

import ch.supsi.application.Image.ImageBusinessInterface;
import ch.supsi.business.strategy.ArgbConvertStrategy;

public class ImageBusiness implements ImageBusinessInterface {

    /* instance field */
    private long[][] argbPixels;
    private final String filePath;

    // Tutti i formati di immagine
    // (PNG, BMP, GIF, JPEG, TIFF, ICO, PPM, PGM, PBM etc...)
    // hanno un magicNumber che permette al software di riconoscerne
    // il tipo senza basarsi sull'estensione. Di conseguenza va incluso
    // come attributo di un immagine generica
    // P + valore nei PNM è semplicemente codificato in ascii e non in byte
    private final String magicNumber;

    public ImageBusiness(long[][] original, String path, String magicNumber, ArgbConvertStrategy strategy) {
        this.argbPixels = createArgbMatrix(original, strategy);
        filePath = path;

        this.magicNumber = magicNumber;
    }


    public long[][] getPixels() {
        return argbPixels;
    }


    @Override
    public void setPixels(long[][] rotatedPixels) {
        argbPixels = rotatedPixels;
    }

    @Override
    public long[][] returnOriginalMatrix(ArgbConvertStrategy strategy) {
        int height = argbPixels.length;
        if (argbPixels.length == 0) {
            return new long[0][0];
        }

        int width = argbPixels[0].length;

        long[][] pixels = new long[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x ++) {
                //estrae i valori originali dei canali
                pixels[y][x] = strategy.toOriginal(this.argbPixels[y][x]);
            }
        }

        return pixels;
    }


    public int getWidth() {
        //argbPixels sempre diverso da null perchè viene convertito in long[0][0]
        //dal costruttore in caso sia null
        return  argbPixels.length > 0  ? argbPixels[0].length : 0;
    }


    public int getHeight() {
        return argbPixels.length;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getMagicNumber() {
        return magicNumber;
    }

    private long[][] createArgbMatrix(long[][] original, ArgbConvertStrategy strategy) {
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
}

