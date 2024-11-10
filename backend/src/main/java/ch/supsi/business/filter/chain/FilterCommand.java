package ch.supsi.business.filter.chain;

import ch.supsi.application.image.ImageBusinessInterface;

public interface FilterCommand {
    void execute(ImageBusinessInterface image);
    String getName();
}
