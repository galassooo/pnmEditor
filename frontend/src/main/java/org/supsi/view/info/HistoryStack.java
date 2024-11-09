package org.supsi.view.info;

import org.supsi.model.filters.FilterModel;
import org.supsi.model.filters.IFilterModel;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class HistoryStack {

    @FXML
    private ListView<String> list;

    private IFilterModel model = FilterModel.getInstance();

    @FXML
    private void initialize() {
        ObservableList<String> reversedList = FXCollections.observableArrayList(model.getLastApplied());
        FXCollections.reverse(reversedList);

        list.setItems(reversedList);

        model.getLastApplied().addListener((ListChangeListener<String>) change -> {
            reversedList.setAll(model.getLastApplied());
            FXCollections.reverse(reversedList);
        });
    }
}