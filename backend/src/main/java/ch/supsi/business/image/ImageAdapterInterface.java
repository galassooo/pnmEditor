package ch.supsi.business.image;

import ch.supsi.application.image.ImageBusinessInterface;

public interface ImageAdapterInterface {
    ImageBusinessInterface argbToRaw(ImageBusinessInterface rawImage);
    ImageBusinessInterface rawToArgb(ImageBusinessInterface rawImage);
}
