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

/**
 * Represents the information stack view that displays a list of log entries.
 * The view listens for updates in the log model and dynamically updates the displayed list.
 * Each log entry is styled based on its type.
 */
public class InformationStack {

    @FXML
    private ListView<LogEntry> list;


    /**
     * Initializes the information stack view.
     * Sets up the {@link ListView} to display log entries and listens for changes in the log model.
     * Configures a custom cell factory for styling log entries based on their type.
     */
    @FXML
    private void initialize() {
        ILoggerModel model = LoggerModel.getInstance();

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
                        super.updateItem(item, empty); //not null item, throws ex before this point
                        if (empty) {
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
