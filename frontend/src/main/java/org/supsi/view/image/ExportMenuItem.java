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


public class ExportMenuItem{

    private final EventPublisher publisher;

    @FXML
    private Menu exportMenu;

    private ExportMenuItem() {
        publisher = EventManager.getPublisher();
    }

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
