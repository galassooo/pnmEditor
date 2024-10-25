package ch.supsi.dispatcher;

import ch.supsi.controller.filter.FilterController;
import ch.supsi.controller.filter.IFilterController;
import ch.supsi.controller.image.ImageController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;


public class FilterDispatcher implements ImageLoadedListener {

    private final IFilterController filterController = FilterController.getInstance();


    @FXML
    public Button activate;
    @FXML
    private void initialize(){
        ImageController.getInstance().subscribe(this);
    }

    public void activatePipeline(ActionEvent event) {
        filterController.activatePipeline();
    }
    public void rotationLeft(ActionEvent event) {
        filterController.addRotationLeft();

    }
    public void rotationRight(ActionEvent event) {
        filterController.addRotationRight();
    }
    public void mirror(ActionEvent event) {
        filterController.mirror();
    }
    public void negative(ActionEvent event) {
        filterController.negative();
    }

    //NON MI PIACE!
    @Override
    public void onImageLoaded(){
        activate.setDisable(false);
    }
}
