package org.supsi.view.filter;

import org.supsi.model.filters.FilterModel;
import org.supsi.model.filters.IFilterModel;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FilterMenuItem implements IFilterEvent{

    @FXML
    private Menu menu;

    private final List<FilterUpdateListener> listeners = new ArrayList<>();

    @FXML
    void initialize() {
        IFilterModel model = FilterModel.getInstance();

        Map<String, String> filtersKeyValues = model.getFiltersKeyValues();

        filtersKeyValues.forEach((filterKey, translatedFilter) -> {
            MenuItem item = new MenuItem(translatedFilter);

            item.setOnAction(actionEvent ->
                    listeners.forEach(listener -> listener.onFilterAdded(filterKey))
            );

            menu.getItems().add(item);
        });
    }

    /**
     * Registers a FilterUpdateListener to receive filter update events.
     *
     * @param listener The listener to register.
     */
    @Override
    public void registerListener(FilterUpdateListener listener) {
        listeners.add(listener);
    }

    /**
     * De registers a FilterUpdateListener from receiving filter update events.
     *
     * @param listener The listener to deregister.
     */
    @Override
    public void deregisterListener(FilterUpdateListener listener) {
        listeners.remove(listener);
    }

}
