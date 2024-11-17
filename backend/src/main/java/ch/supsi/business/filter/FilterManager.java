package ch.supsi.business.filter;

import ch.supsi.application.filters.FilterPipelineInterface;
import ch.supsi.application.image.WritableImage;
import ch.supsi.business.filter.chain.FilterChainLink;
import ch.supsi.business.filter.chain.command.FilterCommand;

/**
 * Manages a chain of filters applied to an image. Implements the Singleton pattern to ensure
 * a single instance is used throughout the application. Filters are organized in a chain structure
 * and can be executed sequentially on an image.
 */
public class FilterManager implements FilterPipelineInterface {

    private static FilterManager instance;
    private FilterChainLink pipeline;
    private int size = 0;

    private FilterManager() {
    }

    /**
     * Retrieves the Singleton instance of {@link FilterManager}.
     *
     * @return the Singleton instance
     */
    public static FilterManager getInstance() {
        if (instance == null) {
            instance = new FilterManager();
        }
        return instance;
    }

    /**
     * Executes the entire filter pipeline on the given image.
     * Clears the pipeline after execution.
     *
     * @param image the {@link WritableImage} to process
     */
    @Override
    public void executePipeline(WritableImage image) {
        if (image == null || pipeline == null) return;
        pipeline.execute(image);
        pipeline = null;
    }

    /**
     * Adds a filter to the end of the pipeline.
     *
     * @param command the {@link FilterCommand} to add
     */
    @Override
    public void addFilter(FilterCommand command) {
        FilterChainLink link = new FilterChainLink(command);
        if (pipeline == null) {
            pipeline = link;
        } else {
            size++;
            FilterChainLink current = pipeline;
            while (current.getNext() != null) {
                current = current.getNext();
            }
            current.setNext(link);
        }
    }

    /**
     * Adds a filter to a specific position in the pipeline.
     *
     * @param command the {@link FilterCommand} to add
     * @param index   the position in the pipeline where the filter should be inserted
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    @Override
    public void addFilter(FilterCommand command, int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("invalid index");
        }

        FilterChainLink newLink = new FilterChainLink(command);

        if (index == 0) {
            newLink.setNext(pipeline);
            pipeline = newLink;
            size++;
            return;
        }

        FilterChainLink current = pipeline;
        for (int i = 0; i < index - 1; i++) {
            if (current == null) {
                throw new IndexOutOfBoundsException("invalid index");
            }
            current = current.getNext();
        }

        size++;
        newLink.setNext(current.getNext());
        current.setNext(newLink);
    }

    /**
     * Removes a filter from a specific position in the pipeline.
     *
     * @param index the position of the filter to remove
     * @return the name of the removed filter
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    @Override
    public String remove(int index) {
        if (index < 0 || pipeline == null) {
            throw new IndexOutOfBoundsException("invalid index");
        }

        if (index == 0) {
            String name = pipeline.getName();
            pipeline = pipeline.getNext();
            size--;
            return name;
        }

        FilterChainLink current = pipeline;
        for (int i = 0; i < index - 1; i++) {
            if (current.getNext() == null) {
                throw new IndexOutOfBoundsException("invalid index");
            }
            current = current.getNext();
        }

        if (current.getNext() != null) {
            String name = current.getNext().getName();
            current.setNext(current.getNext().getNext());
            size--;
            return name;
        }

        throw new IndexOutOfBoundsException("invalid index");
    }

    @Override
    public int getSize() {
        return size;
    }
}