package ch.supsi.application.filters;

import ch.supsi.application.image.ImageBusinessInterface;
import ch.supsi.application.state.EditorStateManager;
import ch.supsi.business.filter.FilterFactory;
import ch.supsi.business.filter.FilterManager;
import ch.supsi.business.filter.chain.command.FilterCommand;
import ch.supsi.business.state.BusinessEditorState;
import java.util.List;

public class FilterApplication {

    private static FilterApplication myself;

    private final List<String> allFilters = FilterFactory.getFilters();
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
        return List.copyOf(allFilters);
    }

    public void addFilterToPipeline(String filterName){
        FilterCommand filter = FilterFactory.getFilter(filterName);
        model.addFilter(filter);
        stateManager.onFilterAdded();
    }
    public void processFilterPipeline(ImageBusinessInterface image){
        model.executePipeline(image);

        stateManager.onFilterProcessed();
    }

    public void add(String filterName, int index){
        FilterCommand filter = FilterFactory.getFilter(filterName);
        model.addFilter(filter, index);
        stateManager.onFilterAdded();
    }
    public String remove(int index){
        if(model.getSize() == 1){
            stateManager.onFiltersRemoved();
        }
        return model.remove(index); //not nullable -> se errore lancia ex
    }
}
