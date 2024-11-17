package ch.supsi.business.filter.chain.command;

import ch.supsi.application.image.WritableImage;

public interface FilterCommand {
    void execute(WritableImage image);
    String getName();
}
