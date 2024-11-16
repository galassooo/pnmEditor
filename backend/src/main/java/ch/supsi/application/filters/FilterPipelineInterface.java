package ch.supsi.application.filters;


import ch.supsi.application.image.ImageBusinessInterface;
import ch.supsi.business.filter.chain.command.FilterCommand;

public interface FilterPipelineInterface {

    void  addFilter(FilterCommand filterStrategy);
    void  addFilter(FilterCommand filterStrategy, int index);
    String remove(int index);
    void executePipeline(ImageBusinessInterface image);
    int getSize();
}
