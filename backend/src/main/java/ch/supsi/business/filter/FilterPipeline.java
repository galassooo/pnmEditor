package ch.supsi.business.filter;

import ch.supsi.application.filters.FilterPipelineInterface;
import ch.supsi.business.filter.strategy.NamedFilterStrategy;

import java.util.ArrayList;
import java.util.List;

public class FilterPipeline implements FilterPipelineInterface {

    private static FilterPipeline filterPipeline;
    private final List<NamedFilterStrategy> pipeline = new ArrayList<>();

    public static FilterPipeline getInstance() {
        if(filterPipeline == null) {
            filterPipeline = new FilterPipeline();
        }
        return filterPipeline;
    }

    private FilterPipeline(){}

    public void  addFilter(NamedFilterStrategy filterStrategy){
        pipeline.add(filterStrategy);
        pipeline.forEach(System.out::println);
    }
    public void clear(){
        pipeline.clear();
    }
    public List<NamedFilterStrategy> getPipeline() {
        return pipeline;
    }

    public void addFilter(NamedFilterStrategy filterStrategy, int index){
        pipeline.add(index, filterStrategy);
    }

    public void remove(int index){
        pipeline.remove(index);
    }
}
