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


    /* writable image */ //-> crea nuova interface
    ImageBusinessInterface persist(String path) throws IOException, IllegalAccessException;
    ImageBusinessInterface export(String extension, String path) throws IOException, IllegalAccessException;
    void setPixels(long[][] rotatedPixels);
}

