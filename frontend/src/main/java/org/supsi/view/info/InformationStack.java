package org.supsi.view.info;

import org.supsi.model.info.ILoggerModel;
import org.supsi.model.info.LogEntry;
import org.supsi.model.info.LoggerModel;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

public class InformationStack {

    private ILoggerModel model = LoggerModel.getInstance();

    @FXML
    private ListView<LogEntry> list;

    @FXML
    private void initialize() {
        model.getLogs().addListener((ListChangeListener<LogEntry>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    list.getItems().addAll(0, change.getAddedSubList());
                }
            }
        });

        list.setCellFactory(new Callback<>() {
            @Override
            public ListCell<LogEntry> call(ListView<LogEntry> listView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(LogEntry item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            Label label = new Label("[" + item.type().name() + "] ");
                            Label message = new Label(item.message());

                            // Imposta lo stile inline per il colore del testo usando hexColor
                            label.setStyle("-fx-text-fill: " + item.getHexColor() + ";");

                            // Layout orizzontale con etichetta colorata e messaggio normale
                            HBox hBox = new HBox(label, message);
                            setGraphic(hBox);
                        }
                    }
                };
            }
        });
    }
}
