package ch.supsi.business.image;

import ch.supsi.application.image.WritableImage;
import java.io.IOException;

public interface ImageDataAccess {
    WritableImage read(String path) throws IOException;
    WritableImage write(WritableImage image) throws IOException ;
}

