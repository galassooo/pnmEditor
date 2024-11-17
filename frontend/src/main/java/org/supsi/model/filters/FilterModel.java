package org.supsi.model.filters;

import ch.supsi.application.filters.FilterApplication;
import ch.supsi.application.image.ImageApplication;
import ch.supsi.application.translations.TranslationsApplication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents the model for managing image filters and their pipeline.
 * Provides methods to add, remove, and process filters, and tracks applied filters.
 */
public class FilterModel implements IFilterModel {

    private static FilterModel mySelf;
    private final FilterApplication application;
    private final ImageApplication imgApplication;
    private final TranslationsApplication translationsApplication;

    private final ObservableList<String> filterPipeline;
    private final ObservableList<String> appliedFilters;

    /**
     * Constructs a new {@code FilterModel} instance.
     * Initializes the application controllers and observable lists for filter pipelines and applied filters.
     */
    protected FilterModel() {
        application = FilterApplication.getInstance();
        imgApplication = ImageApplication.getInstance();
        translationsApplication = TranslationsApplication.getInstance();

        filterPipeline = FXCollections.observableArrayList();
        appliedFilters = FXCollections.observableArrayList();
    }

    /**
     * Retrieves the singleton instance of the {@code FilterModel}.
     *
     * @return the singleton instance of {@code FilterModel}
     */
    public static FilterModel getInstance() {
        if (mySelf == null) {
            mySelf = new FilterModel();
        }
        return mySelf;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObservableList<String> getFilterPipeline() {
        return filterPipeline;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObservableList<String> getLastApplied() {
        return appliedFilters;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addFilterToPipeline(String filter) {
        filterPipeline.add(translationsApplication.translate(filter).orElse("N/A")); // Local copy for observers
        application.addFilterToPipeline(filter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processFilters() {
        appliedFilters.addAll(filterPipeline);
        filterPipeline.clear();
        application.processFilterPipeline(
                imgApplication.getCurrentImage().orElseThrow(() -> new IllegalStateException("Image not set"))
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void moveFilter(int fromIndex, int toIndex) {
        if (fromIndex != toIndex) {
            var translatedItem = filterPipeline.remove(fromIndex);
            filterPipeline.add(toIndex, translatedItem);
            application.add(application.remove(fromIndex), toIndex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getFiltersKeyValues() {
        return application.getAllAvailableFilters().stream()
                .collect(Collectors.toMap(
                        filter -> filter,
                        f -> translationsApplication.translate(f).orElse(f + " N/A") // Unique translation
                ));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeFilter(int index) {
        filterPipeline.remove(index);
        application.remove(index);
    }
}
