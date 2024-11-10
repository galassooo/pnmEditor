package org.supsi.view.image;

import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import org.supsi.model.filters.FilterModel;
import org.supsi.model.filters.IFilterModel;
import org.supsi.model.image.IImageModel;
import org.supsi.model.image.ImageModel;

import java.util.List;
import java.util.Map;

public class ExportMenuItem {

    @FXML
    private Menu exportMenu;


    @FXML
    private void initialize() {
        IImageModel model = ImageModel.getInstance();

        List<String> filtersKeyValues = model.getSupportedExtensions();

        filtersKeyValues.forEach(extension -> {
            MenuItem item = new MenuItem(extension);

            item.setOnAction(actionEvent ->{


            });

            exportMenu.getItems().add(item);
        });
    }
}
