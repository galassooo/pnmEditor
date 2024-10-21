package ch.supsi.application.image;

import ch.supsi.business.strategy.ArgbConvertStrategy;

import java.io.IOException;

public interface ImageBusinessInterface {

    String getFilePath();
    int getWidth();
    int getHeight();
    long[][] getPixels();
    long[][] returnOriginalMatrix(ArgbConvertStrategy strategy);
    String getMagicNumber();
    ImageBusinessInterface persist(String path) throws IOException, IllegalAccessException;
    /* modifiable image */ //-> crea nuova interface
    void setPixels(long[][] rotatedPixels);
}

