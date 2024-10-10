package ch.supsi.business.Image;

import ch.supsi.application.Image.ImageBusinessInterface;

public interface ImageDataAccess {

    ImageBusinessInterface readImage(String path);
    ImageBusinessInterface writeImage(String path);
}
