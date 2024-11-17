package ch.supsi.application.filters;

import ch.supsi.application.image.WritableImage;
import ch.supsi.business.filter.chain.command.FilterCommand;

/**
 * The {@code FilterPipelineInterface} interface defines the operations for managing a filter
 * pipeline. Implementing classes must provide mechanisms to add, remove, and execute filters
 * on images, as well as retrieve the current size of the pipeline.
 */
public interface FilterPipelineInterface {

    /**
     * Adds a filter to the end of the filter pipeline.
     *
     * @param filterStrategy the filter to add, represented as a {@code FilterCommand}.
     */
    void addFilter(FilterCommand filterStrategy);

    /**
     * Adds a filter at a specific index in the filter pipeline.
     *
     * @param filterStrategy the filter to add, represented as a {@code FilterCommand}.
     * @param index the position in the pipeline where the filter should be added.
     */
    void addFilter(FilterCommand filterStrategy, int index);

    /**
     * Removes a filter from the filter pipeline at a specific index.
     *
     * @param index the position of the filter to remove.
     * @return the name of the removed filter.
     */
    String remove(int index);

    /**
     * Executes the filter pipeline on the given image.
     *
     * @param image the {@code ImageBusinessInterface} representing the image to process.
     */
    void executePipeline(WritableImage image);

    /**
     * Retrieves the current number of filters in the filter pipeline.
     *
     * @return the number of filters in the pipeline.
     */
    int getSize();
}
