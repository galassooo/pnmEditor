package ch.supsi.business.Image;

import ch.supsi.application.Image.ImageBusinessInterface;

import java.io.IOException;

public interface ImageDataAccess {

    ImageBusinessInterface read(String path) throws IOException;
    ImageBusinessInterface write(ImageBusinessInterface image, String path) throws IOException ;
}

