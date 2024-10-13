package ch.supsi.business.Image;

import ch.supsi.application.Image.ImageBusinessInterface;
import ch.supsi.business.strategy.ArgbConvertStrategy;

public class ImageBusiness implements ImageBusinessInterface {

    /* instance field */
    private long[][] argbPixels;
    private int width;
    private int height;
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
        this.height = original.length;
        this.width = height == 0? 0 : original[0].length;
        filePath = path;

        this.magicNumber = magicNumber;
    }

    public ImageBusiness(long[][] original, String path, String magicNumber) {
        this.argbPixels = original;
        this.height = original.length;
        this.width = height == 0? 0 : original[0].length;
        filePath = path;

        this.magicNumber = magicNumber;
    }

    public long[][] getPixels() {
        return argbPixels;
    }

    @Override
    public long[][] returnOriginalMatrix(ArgbConvertStrategy strategy) {
        if (height == 0) {
            return new long[0][0];
        }

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
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getMagicNumber() {
        return magicNumber;
    }

    private long[][] createArgbMatrix(long[][] original, ArgbConvertStrategy strategy) {
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

