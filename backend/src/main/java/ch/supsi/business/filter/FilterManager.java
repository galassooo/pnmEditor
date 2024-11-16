package ch.supsi.business.filter;

import ch.supsi.application.filters.FilterPipelineInterface;
import ch.supsi.application.image.ImageBusinessInterface;
import ch.supsi.business.filter.chain.FilterChainLink;
import ch.supsi.business.filter.chain.command.FilterCommand;

public class FilterManager implements FilterPipelineInterface {
    private static FilterManager instance;
    private FilterChainLink pipeline;
    private int size = 0;

    public static FilterManager getInstance() {
        if(instance == null) {
            instance = new FilterManager();
        }
        return instance;
    }

    private FilterManager() {}

    @Override
    public void executePipeline(ImageBusinessInterface image) {
        if (image == null || pipeline == null) return;
        pipeline.execute(image);
        pipeline = null;
    }

    @Override
    public void addFilter(FilterCommand command) {
        FilterChainLink link = new FilterChainLink(command);
        if(pipeline == null) {
            pipeline = link;
        } else {
            size++;
            FilterChainLink current = pipeline;
            while(current.getNext() != null) {
                current = current.getNext();
            }
            current.setNext(link);
        }
    }

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