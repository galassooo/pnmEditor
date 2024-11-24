package org.supsi.view.filter;

import org.supsi.model.event.EventManager;
import org.supsi.model.event.EventPublisher;
import org.supsi.model.filters.FilterModel;
import org.supsi.model.filters.IFilterModel;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

import java.util.Map;

/**
 * Represents a menu component for selecting and applying filters.
 * Dynamically populates the menu with filter options and handles filter selection events.
 */
public class FilterMenuItem{

    private EventPublisher publisher;

    @FXML
    private Menu menu;



    /**
     * Initializes the filter menu.
     * Populates the menu with filter options and associates actions to publish filter-related events.
     */
    @FXML
    void initialize() {
        publisher = EventManager.getPublisher();
        IFilterModel model = FilterModel.getInstance();

        Map<String, String> filtersKeyValues = model.getFiltersKeyValues();

        filtersKeyValues.forEach((filterKey, translatedFilter) -> {
            MenuItem item = new MenuItem(translatedFilter);

            item.setOnAction(actionEvent ->
                    publisher.publish(new FilterEvent.FilterAddRequested(filterKey))
            );

            menu.getItems().add(item);
        });
    }
}
