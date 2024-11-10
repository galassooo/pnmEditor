package ch.supsi.application.filters;

import ch.supsi.application.image.ImageBusinessInterface;
import ch.supsi.application.state.EditorStateManager;
import ch.supsi.business.filter.FilterFactory;
import ch.supsi.business.filter.FilterManager;
import ch.supsi.business.filter.command.FilterCommand;
import ch.supsi.business.state.BusinessEditorState;

import java.util.List;
import java.util.Map;

public class FilterApplication {

    private static FilterApplication myself;

    private final Map<String, FilterCommand> allFilters = FilterFactory.getFilters();
    private final FilterPipelineInterface model = FilterManager.getInstance();
    private final EditorStateManager stateManager = BusinessEditorState.getInstance();

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
        FilterCommand filter = allFilters.get(filterName);
        model.addFilter(filter);
        stateManager.onFilterAdded();
    }
    public void processFilterPipeline(ImageBusinessInterface image){
        model.executePipeline(image);

        stateManager.onFilterProcessed();
    }

    public void add(String filterName, int index){
        FilterCommand filter = allFilters.get(filterName);
        model.addFilter(filter, index);

        stateManager.onFilterAdded();
    }
    public void remove(int index){
        model.remove(index);

        if(model.getPipeline().isEmpty()){
            stateManager.onFilterRemoved();
        }
    }
}
