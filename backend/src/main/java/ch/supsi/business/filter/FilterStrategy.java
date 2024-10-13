package ch.supsi.business.filter;

import ch.supsi.application.Image.ImageBusinessInterface;

public interface FilterStrategy {

    void applyFilter(ImageBusinessInterface img);
}
