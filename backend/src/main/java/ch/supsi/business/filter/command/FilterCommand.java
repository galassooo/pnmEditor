package ch.supsi.business.filter.command;

import ch.supsi.application.image.ImageBusinessInterface;

public interface FilterCommand {
    void execute(ImageBusinessInterface image);
    String getName();
}
