package ch.supsi.dispatcher;

import ch.supsi.controller.filter.FilterController;
import ch.supsi.controller.image.ImageController;
import ch.supsi.controller.image.ImageLoadedListener;
import ch.supsi.view.filter.FilterUpdateListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;


public class FilterDispatcher implements ImageLoadedListener {

    private final FilterUpdateListener filterController = FilterController.getInstance();


    @FXML
    public Button activate;
    @FXML
    private void initialize(){
        ImageController.getInstance().subscribe(this);
    }

    public void activatePipeline(ActionEvent event) {
        filterController.onFiltersActivated();
    }
    public void rotationLeft(ActionEvent event) {
        filterController.onFilterAdded("Rotate_Left");
    }
    public void rotationRight(ActionEvent event) {
        filterController.onFilterAdded("Rotate_Right");
    }
    public void mirror(ActionEvent event) {
        filterController.onFilterAdded("Mirror");
    }
    public void negative(ActionEvent event) {
        filterController.onFilterAdded("Negative");
    }

    //NON MI PIACE!
    @Override
    public void onImageLoaded(){
        activate.setDisable(false);
    }
}
