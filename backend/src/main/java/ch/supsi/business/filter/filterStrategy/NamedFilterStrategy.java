package ch.supsi.business.filter.filterStrategy;

import ch.supsi.application.image.ImageBusinessInterface;

public interface NamedFilterStrategy {

    void applyFilter(ImageBusinessInterface img);
    String getName();
}
