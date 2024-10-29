package ch.supsi.application.image;

import ch.supsi.business.strategy.ConvertStrategy;

import java.io.IOException;

public interface ImageBusinessInterface {

    String getFilePath();
    int getWidth();
    int getHeight();
    long[][] getPixels();
    String getName();
    String getMagicNumber();


    ImageBusinessInterface persist(String path) throws IOException, IllegalAccessException;

    /* modifiable image */ //-> crea nuova interface
    void setPixels(long[][] rotatedPixels);
}

