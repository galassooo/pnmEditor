package ch.supsi.model.filters;

import ch.supsi.application.filters.FilterApplication;
import ch.supsi.application.image.ImageApplication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class FilterModel implements IFilterModel{

    private static FilterModel mySelf;
    private final FilterApplication application = FilterApplication.getInstance();
    private final ImageApplication imgApplication = ImageApplication.getInstance();

    private final ObservableList<String> filterPipeline = FXCollections.observableArrayList();

    public static FilterModel getInstance(){
        if(mySelf==null){
            mySelf=new FilterModel();
        }
        return mySelf;

    }

    protected FilterModel(){

    }

    @Override
    public ObservableList<String> getFilterPipeline() {
        return filterPipeline;
    }

    public void addFilterToPipeline(String filter){

        filterPipeline.add(filter); //local copy for observers

        application.addFilterToPipeline(filter);

    }

    public void processFilters(){
        filterPipeline.clear();
        application.processFilterPipeline(imgApplication.getCurrentImage());
    }
}
