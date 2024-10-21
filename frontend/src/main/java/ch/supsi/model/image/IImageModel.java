package ch.supsi.model.image;

import ch.supsi.application.Image.ImageBusinessInterface;

import java.io.IOException;

public interface IImageModel {
    void readImage(String path) throws IOException;
    ImageBusinessInterface getImage();

}
