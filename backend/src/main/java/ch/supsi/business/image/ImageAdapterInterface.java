package ch.supsi.business.image;

import ch.supsi.application.image.ImageBusinessInterface;

public interface ImageAdapterInterface {
    long[][] argbToRaw(long[][] rawImage);
    long[][] rawToArgb(long[][] rawImage);
}
