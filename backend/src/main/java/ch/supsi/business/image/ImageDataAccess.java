package ch.supsi.business.image;

import ch.supsi.application.image.ImageBusinessInterface;

import java.io.IOException;

public interface ImageDataAccess {

    ImageBusinessInterface read(String path) throws IOException;
    ImageBusinessInterface write(ImageBusinessInterface image) throws IOException ;
}

