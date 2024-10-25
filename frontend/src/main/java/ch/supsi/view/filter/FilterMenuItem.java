package ch.supsi.view.filter;

import ch.supsi.model.filters.FilterModel;
import ch.supsi.model.filters.IFilterModel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class FilterMenuItem implements IFilterEvent{

    @FXML
    private Menu menu;

    private final List<FilterUpdateListener> listeners = new ArrayList<>();

    @FXML
    void initialize() {
        IFilterModel model = FilterModel.getInstance();
        List<String> allFilters = model.getAllFilters();
        allFilters.forEach(filter -> {
            MenuItem item = new MenuItem(filter);
            item.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent actionEvent) {
                    listeners.forEach(listener -> listener.onFilterAdded(filter));
                }
            });
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
