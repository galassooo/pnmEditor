package ch.supsi.application.filters;


import ch.supsi.application.image.ImageBusinessInterface;
import ch.supsi.business.filter.chain.FilterChainLink;
import ch.supsi.business.filter.chain.FilterCommand;

import java.util.List;

public interface FilterPipelineInterface {

    void  addFilter(FilterChainLink filterStrategy);
    void  addFilter(FilterChainLink filterStrategy, int index);
    void remove(int index);
    void executePipeline(ImageBusinessInterface image);
}
