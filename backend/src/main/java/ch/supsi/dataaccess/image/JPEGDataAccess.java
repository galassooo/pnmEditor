package ch.supsi.dataaccess.image;

import ch.supsi.ImageAccess;
import ch.supsi.application.image.ImageBusinessInterface;
import ch.supsi.business.image.ImageDataAccess;

import java.io.IOException;
import java.util.List;

@ImageAccess(magicNumber = {"JPEG"})
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
