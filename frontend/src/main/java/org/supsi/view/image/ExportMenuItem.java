package org.supsi.view.image;

import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import org.supsi.model.event.EventManager;
import org.supsi.model.event.EventPublisher;
import org.supsi.model.image.IImageModel;
import org.supsi.model.image.ImageModel;
import org.supsi.model.state.IStateModel;
import org.supsi.model.state.StateModel;

import java.util.List;

/**
 * Represents a menu component for exporting images in different formats.
 * Dynamically populates the export menu with supported file extensions and handles export events.
 */
public class ExportMenuItem{

    private final EventPublisher publisher;

    @FXML
    private Menu exportMenu;

    /**
     * Constructs a new {@code ExportMenuItem} and initializes the event publisher.
     */
    private ExportMenuItem() {
        publisher = EventManager.getPublisher();
    }

    /**
     * Initializes the export menu.
     * Populates the menu with supported file extensions and binds their enabled state
     * to the application's export capability.
     */
    @FXML
    private void initialize() {
        IImageModel model = ImageModel.getInstance();
        IStateModel stateModel = StateModel.getInstance();

        List<String> filtersKeyValues = model.getSupportedExtensions();

        filtersKeyValues.forEach(extension -> {
            MenuItem item = new MenuItem(extension);

            item.disableProperty().bind(stateModel.canExportProperty().not());
            item.setOnAction(actionEvent -> publisher.publish(new ExportEvent.ExportRequested(extension)));

            exportMenu.getItems().add(item);
        });
    }
}
