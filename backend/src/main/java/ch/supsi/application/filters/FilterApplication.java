package ch.supsi.application.filters;

import ch.supsi.application.image.ImageBusinessInterface;
import ch.supsi.business.filter.FilterFactory;
import ch.supsi.business.filter.FilterPipeline;
import ch.supsi.business.filter.strategy.NamedFilterStrategy;

import java.util.List;
import java.util.Map;

public class FilterApplication {

    private static FilterApplication myself;

    private final Map<String, NamedFilterStrategy> allFilters = FilterFactory.getFilters();

    private final FilterPipelineInterface model = FilterPipeline.getInstance();

    public static  FilterApplication getInstance(){
        if (myself==null){
            myself = new FilterApplication();
        }
        return myself;
    }

    private FilterApplication() {}

    public List<String> getAllAvailableFilters(){ //si occupa della presentazione dei dati
        var allFiltersNames = allFilters.keySet();

        return allFiltersNames.stream().toList();
    }

    public void addFilterToPipeline(String filterName){
        NamedFilterStrategy filter = allFilters.get(filterName);
        model.addFilter(filter);
    }
    public void processFilterPipeline(ImageBusinessInterface image){

        model.getPipeline().forEach(filter ->{
            filter.applyFilter(image);
        });
        model.clear();
    }

    public void clearPipeline(){
        model.clear();
    }

    public void add(String filterName, int index){
        NamedFilterStrategy filter = allFilters.get(filterName);
        model.addFilter(filter, index);
    }
    public void remove(int index){
        model.remove(index);
    }
}
