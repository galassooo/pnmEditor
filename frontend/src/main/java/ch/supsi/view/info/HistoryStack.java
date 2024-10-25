package ch.supsi.view.info;

import ch.supsi.model.filters.FilterModel;
import ch.supsi.model.filters.IFilterModel;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.util.Collections;

public class HistoryStack {

    @FXML
    private ListView<String> list;

    private IFilterModel model = FilterModel.getInstance();

    @FXML
    private void initialize() {
        // Crea una copia invertita di getLastApplied()
        ObservableList<String> reversedList = FXCollections.observableArrayList(model.getLastApplied());
        FXCollections.reverse(reversedList);

        // Collega la lista invertita alla ListView
        list.setItems(reversedList);

        // Ascolta i cambiamenti sulla lista originale e aggiorna l'ordine invertito
        model.getLastApplied().addListener((ListChangeListener<String>) change -> {
            reversedList.setAll(model.getLastApplied());
            FXCollections.reverse(reversedList);
        });
    }
}