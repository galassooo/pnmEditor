package ch.supsi.business.filter;

import ch.supsi.application.image.ImageBusinessInterface;

public interface FilterStrategy {

    void applyFilter(ImageBusinessInterface img);
}
