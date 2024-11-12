package ch.supsi.business.filter;

import ch.supsi.application.filters.FilterPipelineInterface;
import ch.supsi.application.image.ImageBusinessInterface;
import ch.supsi.business.filter.chain.FilterChainLink;

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
            size++;
            FilterChainLink link = pipeline;
            while((link.getNext()) != null){
                link = link.getNext();
            }
            link.setNext(command);
        }
    }

    @Override
    public void addFilter(FilterChainLink command, int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("invalid index");
        }
        if (index == 0) {
            command.setNext(pipeline);
            pipeline = command;
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
        command.setNext(current.getNext());
        current.setNext(command);
    }

    @Override
    public String remove(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("invalid index");
        }

        if(pipeline == null) {
            return null;
        }

        if (index == 0 ) {
            --size;
            String tmpName = pipeline.getName();
            pipeline = pipeline.getNext();
            return tmpName;
        }
        FilterChainLink current = pipeline;
        for (int i = 0; i < index - 1; i++) {
            if (current == null || current.getNext() == null) {
                throw new IndexOutOfBoundsException("invalid index");
            }
            current = current.getNext();
        }

        if (current.getNext() != null) {
            --size;
            current.setNext(current.getNext().getNext());
        }
        return current.getName();
    }

    @Override
    public int getSize(){
        return size;
    }


    //utile per debug
    private void print(){
        System.out.println("LIST: ");
        for(FilterChainLink link = pipeline; link != null; link = link.getNext()){
            System.out.println(link);
        }
    }
}