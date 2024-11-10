package org.supsi.model.image;

import ch.supsi.application.image.ImageBusinessInterface;

import java.io.IOException;
import java.util.List;

public interface IImageModel {
    void readImage(String path) throws IOException, IllegalAccessException;
    void writeImage(String path) throws IOException, IllegalAccessException;
    String getImageName();
    long[][] getImagePixels();
    List<String> getSupportedExtensions();
    void export(String extension, String path) throws IOException, IllegalAccessException;
}
