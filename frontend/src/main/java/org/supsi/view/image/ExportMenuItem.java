package org.supsi.view.image;

import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import org.supsi.model.filters.FilterModel;
import org.supsi.model.filters.IFilterModel;
import org.supsi.model.image.IImageModel;
import org.supsi.model.image.ImageModel;
import org.supsi.model.state.IStateModel;
import org.supsi.model.state.StateModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExportMenuItem implements IExportEvent{

    @FXML
    private Menu exportMenu;


    private List<ExportEventListener> listeners = new ArrayList<>();
    @FXML
    private void initialize() {
        IImageModel model = ImageModel.getInstance();
        IStateModel stateModel = StateModel.getInstance();

        List<String> filtersKeyValues = model.getSupportedExtensions();

        filtersKeyValues.forEach(extension -> {
            MenuItem item = new MenuItem(extension);

            item.disableProperty().bind(stateModel.canExportProperty().not());
            item.setOnAction(actionEvent ->{
                listeners.forEach(e -> e.onExportRequested(extension));

            });

            exportMenu.getItems().add(item);
        });
    }

    @Override
    public void registerListener(ExportEventListener listener) {
        listeners.add(listener);
    }

    @Override
    public void deregisterListener(ExportEventListener listener) {
        listeners.remove(listener);
    }
}
