package org.supsi.view.filter;

import org.supsi.model.event.EventManager;
import org.supsi.model.event.EventPublisher;
import org.supsi.model.filters.FilterModel;
import org.supsi.model.filters.IFilterModel;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

import java.util.Map;

public class FilterMenuItem{

    @FXML
    private Menu menu;

    private final EventPublisher publisher = EventManager.getPublisher();

    @FXML
    void initialize() {
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
