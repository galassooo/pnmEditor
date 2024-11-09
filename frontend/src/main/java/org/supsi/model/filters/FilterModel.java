package org.supsi.model.filters;

import ch.supsi.application.filters.FilterApplication;
import ch.supsi.application.image.ImageApplication;
import ch.supsi.application.translations.TranslationsApplication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Map;
import java.util.stream.Collectors;

public class FilterModel implements IFilterModel{

    private static FilterModel mySelf;
    private final FilterApplication application = FilterApplication.getInstance();
    private final ImageApplication imgApplication = ImageApplication.getInstance();
    private final TranslationsApplication translationsApplication = TranslationsApplication.getInstance();

    private final ObservableList<String> filterPipeline = FXCollections.observableArrayList();

    private final ObservableList<String> appliedFilters = FXCollections.observableArrayList();

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

    @Override
    public ObservableList<String> getLastApplied() {
        return appliedFilters;
    }

    public void addFilterToPipeline(String filter){

        filterPipeline.add(TranslationsApplication.getInstance().translate(filter)); //local copy for observers

        application.addFilterToPipeline(filter);
    }

    public void processFilters(){
        appliedFilters.addAll(filterPipeline);

        filterPipeline.clear();
        application.processFilterPipeline(imgApplication.getCurrentImage());
    }

    @Override
    public void moveFilter(int fromIndex, int toIndex) {
        if (fromIndex != toIndex) {
            var filterPipeline = getFilterPipeline();
            var item = filterPipeline.remove(fromIndex);
            filterPipeline.add(toIndex, item);

            application.remove(fromIndex);
            application.add(item, toIndex);
        }
    }

    @Override
    public Map<String, String> getFiltersKeyValues() {
        return application.getAllAvailableFilters().stream()
                .collect(Collectors.toMap(
                        filter -> filter,
                        translationsApplication::translate
                ));
    }

    @Override
    public void removeFilter(int index) {
        filterPipeline.remove(index);
        application.remove(index);
    }

}
