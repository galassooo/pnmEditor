package ch.supsi.dataaccess.image;

import ch.supsi.ImageAccess;
import ch.supsi.application.image.ImageBusinessInterface;
import ch.supsi.business.image.ImageDataAccess;

import java.io.IOException;
import java.util.List;

@ImageAccess(magicNumber = {"JPEG"}, extension = "JPEG")
public class JpegReader implements ImageDataAccess {

    private static JpegReader instance;

    public static JpegReader getInstance() {
        if (instance == null) {
            instance = new JpegReader();
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
