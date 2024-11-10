package ch.supsi.dataaccess.image;

import ch.supsi.annotation.ImageAccess;
import ch.supsi.application.image.ImageBusinessInterface;
import ch.supsi.business.image.ImageDataAccess;

import java.io.IOException;

@ImageAccess(magicNumber = {"Jpeg"})
public class JpegDataAccess implements ImageDataAccess {

    private static JpegDataAccess instance;
    public static JpegDataAccess getInstance() {
        if (instance == null) {
            instance = new JpegDataAccess();
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
