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
    private final FilterApplication application;
    private final ImageApplication imgApplication;
    private final TranslationsApplication translationsApplication;

    private final ObservableList<String> filterPipeline;
    private final ObservableList<String> appliedFilters;


    protected FilterModel(){
        application = FilterApplication.getInstance();
        imgApplication = ImageApplication.getInstance();
        translationsApplication = TranslationsApplication.getInstance();

        filterPipeline = FXCollections.observableArrayList();
        appliedFilters = FXCollections.observableArrayList();
    }

    public static FilterModel getInstance(){
        if(mySelf==null){
            mySelf=new FilterModel();
        }
        return mySelf;

    }

    @Override
    public ObservableList<String> getFilterPipeline() {
        return filterPipeline;
    }

    @Override
    public ObservableList<String> getLastApplied() {
        return appliedFilters;
    }

    @Override
    public void addFilterToPipeline(String filter){
        filterPipeline.add(TranslationsApplication.getInstance().translate(filter).orElse("N/A")); //local copy for observers
        application.addFilterToPipeline(filter);
    }

    @Override
    public void processFilters(){
        appliedFilters.addAll(filterPipeline);

        filterPipeline.clear();
        application.processFilterPipeline(imgApplication.getCurrentImage()
                .orElseThrow(() -> new IllegalStateException("Image not set")));
    }

    @Override
    public void moveFilter(int fromIndex, int toIndex) {
        if (fromIndex != toIndex) {

            var translatedItem = filterPipeline.remove(fromIndex);
            filterPipeline.add(toIndex, translatedItem);

            application.add(application.remove(fromIndex), toIndex);
        }
    }

    @Override
    public Map<String, String> getFiltersKeyValues() {
        return application.getAllAvailableFilters().stream()
                .collect(Collectors.toMap(
                        filter -> filter,
                        f -> translationsApplication.translate(f).orElse(f+" N/A") //unique translation
                ));
    }

    @Override
    public void removeFilter(int index) {
        filterPipeline.remove(index);
        application.remove(index);
    }

}
