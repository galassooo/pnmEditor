package org.supsi.view.info;

import org.supsi.model.filters.FilterModel;
import org.supsi.model.filters.IFilterModel;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

/**
 * Represents a history stack view that displays the list of filters applied in reverse order.
 * The view dynamically updates whenever the list of applied filters changes.
 */
public class HistoryStack {

    @FXML
    private ListView<String> list;

    private IFilterModel model;


    /**
     * Initializes the history stack view.
     * Sets up the {@link ListView} to display filters in reverse order and listens for updates
     * in the underlying filter model to dynamically refresh the view.
     */
    @FXML
    private void initialize() {
        model = FilterModel.getInstance();
        ObservableList<String> reversedList = FXCollections.observableArrayList(model.getLastApplied());
        FXCollections.reverse(reversedList);

        list.setItems(reversedList);

        model.getLastApplied().addListener((ListChangeListener<String>) change -> {
            reversedList.setAll(model.getLastApplied());
            FXCollections.reverse(reversedList);
        });
    }
}