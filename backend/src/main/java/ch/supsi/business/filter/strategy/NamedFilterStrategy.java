package ch.supsi.business.filter.strategy;

import ch.supsi.application.image.ImageBusinessInterface;

public interface NamedFilterStrategy {

    void applyFilter(ImageBusinessInterface img);
    String getCode();
}
