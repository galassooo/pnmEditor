package ch.supsi.business.image;

import ch.supsi.application.image.ImageBusinessInterface;
import ch.supsi.business.strategy.ConvertStrategy;
import ch.supsi.dataaccess.image.DataAccessFactory;
import ch.supsi.dataaccess.image.PGMDataAccess;
import ch.supsi.dataaccess.image.PNMDataAccess;
import ch.supsi.dataaccess.image.PPMDataAccess;

import java.io.File;
import java.io.IOException;

public class ImageBusiness implements ImageBusinessInterface {

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

    //VOLUTAMENTE PACKAGE PRIVATE!!!!! -> obbliga a passare dal builder
    ImageBusiness(long[][] original, String filePath, String magicNumber) {
        this.argbPixels = original;
        this.filePath = filePath;
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
    public ImageBusinessInterface persist(String path) throws IOException, IllegalAccessException {
        ImageDataAccess dac = DataAccessFactory.getInstance(filePath);
        if(path!= null)
            filePath = !filePath.equals(path) ? path : filePath;
        return dac.write(this);
    }

    public ImageBusinessInterface export(String extension, String path) throws IOException, IllegalAccessException {
        ImageDataAccess dac = DataAccessFactory.getInstanceFromExtension(extension);
        String magicNumber = DataAccessFactory.getDefaultMagicNumberFromExtension(extension);

        ImageBusinessInterface exportedImage = new ImageBuilder()
                .withFilePath(!filePath.equals(path) ? path : filePath)
                .withMagicNumber(magicNumber)
                .withPixels(argbPixels)
                .build();

        return dac.write(exportedImage);
    }

    public static ImageBusinessInterface read(String path) throws IOException, IllegalAccessException {
        ImageDataAccess dac = DataAccessFactory.getInstance(path);
        return dac.read(path);
    }

    @Override
    public int getWidth() {
        //argbPixels sempre diverso da null perchè viene convertito in long[0][0]
        //dal builder in caso sia null
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

    @Override
    public String getName(){
        File file = new File(filePath);
        return  file.getName();
    }
}

