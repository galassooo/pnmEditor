package ch.supsi.dataaccess.image;

import ch.supsi.annotation.ImageAccess;
import ch.supsi.annotation.ImageAccessFactory;
import ch.supsi.application.image.ImageBusinessInterface;
import ch.supsi.business.image.ImageDataAccess;

import java.io.IOException;

@ImageAccessFactory
public class JPEGDataAccess implements ImageDataAccess {

    private static JPEGDataAccess instance;

    public static JPEGDataAccess getInstance() {
        if (instance == null) {
            instance = new JPEGDataAccess();
        }
        return instance;
    }

    @Override
    public ImageBusinessInterface read(String path) throws IOException {
        return null;
    }

    @Override
    public ImageBusinessInterface write(ImageBusinessInterface image) throws IOException {
        return null;
    }
}
