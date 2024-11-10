package ch.supsi.business.filter;

import ch.supsi.application.filters.FilterPipelineInterface;
import ch.supsi.application.image.ImageBusinessInterface;
import ch.supsi.business.filter.chain.FilterChainLink;

public class FilterManager implements FilterPipelineInterface {
    private static FilterManager instance;
    private FilterChainLink pipeline;

    public static FilterManager getInstance() {
        if(instance == null) {
            instance = new FilterManager();
        }
        return instance;
    }

    private FilterManager() {}

    @Override
    public void executePipeline(ImageBusinessInterface image) {
        if (image == null) return;
        if(pipeline == null) return;

        pipeline.execute(image);
        pipeline = null;
    }

    @Override
    public void addFilter(FilterChainLink command) {
        if(pipeline == null) {
            pipeline = command;
        }else{
            FilterChainLink link = pipeline;
            while((link.getNext()) != null){
                link = link.getNext();
            }
            link.setNext(command);
        }
        print();
    }

    @Override
    public void addFilter(FilterChainLink command, int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("invalid index");
        }
        if (index == 0) {
            command.setNext(pipeline);
            pipeline = command;
            return;
        }

        FilterChainLink current = pipeline;
        for (int i = 0; i < index - 1; i++) {
            if (current == null) {
                throw new IndexOutOfBoundsException("invalid index");
            }
            current = current.getNext();
        }

        command.setNext(current.getNext());
        current.setNext(command);

        print();
    }

    @Override
    public String remove(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("invalid index");
        }

        if (index == 0) {
            pipeline = pipeline.getNext();
            return pipeline.getName();
        }
        FilterChainLink current = pipeline;
        for (int i = 0; i < index - 1; i++) {
            if (current == null || current.getNext() == null) {
                throw new IndexOutOfBoundsException("invalid index");
            }
            current = current.getNext();
        }

        if (current.getNext() != null) {
            current.setNext(current.getNext().getNext());
        }
        return current.getName();
    }


    //utile per debug
    private void print(){
        System.out.println("LIST: ");
        for(FilterChainLink link = pipeline; link != null; link = link.getNext()){
            System.out.println(link);
        }
    }
}