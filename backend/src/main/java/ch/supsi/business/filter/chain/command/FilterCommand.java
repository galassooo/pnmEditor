package ch.supsi.business.filter.chain.command;

import ch.supsi.application.image.WritableImage;

/**
 * Represents a filter command that can be executed on an image.
 * This interface defines the basic contract for all filter commands,
 * requiring implementation of execution logic and a method to retrieve the filter's name.
 */
public interface FilterCommand {

    /**
     * Executes the filter command on the specified image.
     *
     * @param image the {@link WritableImage} to process
     */
    void execute(WritableImage image);

    /**
     * Retrieves the name of the filter command.
     *
     * @return the name of the filter as a {@link String}
     */
    String getName();
}
