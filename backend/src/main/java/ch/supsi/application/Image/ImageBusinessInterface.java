package ch.supsi.application.Image;

import ch.supsi.business.strategy.ArgbConvertStrategy;

public interface ImageBusinessInterface {

    String getFilePath();
    int getWidth();
    int getHeight();
    long[][] getPixels();
    long[][] returnOriginalMatrix(ArgbConvertStrategy strategy);
    String getMagicNumber();

    /* modifiable image */ //-> crea nuova interface
    void setPixels(long[][] rotatedPixels);
}

