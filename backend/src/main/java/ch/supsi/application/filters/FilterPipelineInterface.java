package ch.supsi.application.filters;

import ch.supsi.business.filter.strategy.NamedFilterStrategy;

import java.util.List;

public interface FilterPipelineInterface {

    void  addFilter(NamedFilterStrategy filterStrategy);
    void clear();
    List<NamedFilterStrategy> getPipeline();
}
