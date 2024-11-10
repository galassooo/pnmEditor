package ch.supsi.application.filters;


import ch.supsi.application.image.ImageBusinessInterface;
import ch.supsi.business.filter.chain.FilterChainLink;

public interface FilterPipelineInterface {

    void  addFilter(FilterChainLink filterStrategy);
    void  addFilter(FilterChainLink filterStrategy, int index);
    String remove(int index);
    void executePipeline(ImageBusinessInterface image);
}
