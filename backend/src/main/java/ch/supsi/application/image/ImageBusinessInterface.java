package ch.supsi.application.image;

import ch.supsi.business.strategy.ConvertStrategy;

import java.io.IOException;

public interface ImageBusinessInterface {

    String getFilePath();
    int getWidth();
    int getHeight();
    long[][] getPixels();

    //ADAPTER ????????????????
    long[][] returnOriginalMatrix(ConvertStrategy strategy);
    String getMagicNumber();
    ImageBusinessInterface persist(String path) throws IOException, IllegalAccessException;
    String getName();
    /* modifiable image */ //-> crea nuova interface
    void setPixels(long[][] rotatedPixels);
}

