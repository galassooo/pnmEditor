package ch.supsi.application.filters;

import ch.supsi.business.filter.strategy.NamedFilterStrategy;

import java.util.List;

public interface FilterPipelineInterface {

    void  addFilter(NamedFilterStrategy filterStrategy);
    void  addFilter(NamedFilterStrategy filterStrategy, int index);
    void remove(int index);
    void clear();
    List<NamedFilterStrategy> getPipeline();
}
