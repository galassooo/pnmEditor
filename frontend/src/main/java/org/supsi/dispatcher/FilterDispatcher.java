package org.supsi.dispatcher;

import org.supsi.controller.filter.FilterController;
import org.supsi.model.state.IStateModel;
import org.supsi.model.state.StateModel;
import org.supsi.view.filter.FilterUpdateListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;


public class FilterDispatcher {

    private final FilterUpdateListener filterController = FilterController.getInstance();

    private final IStateModel stateModel = StateModel.getInstance();

    @FXML
    public Button activate;
    @FXML
    private void initialize(){
        activate.disableProperty().bind(stateModel.canApplyFiltersProperty().not());
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

}
