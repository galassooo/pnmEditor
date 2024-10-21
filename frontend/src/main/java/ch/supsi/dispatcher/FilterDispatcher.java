package ch.supsi.dispatcher;

import ch.supsi.controller.filter.FilterController;
import ch.supsi.controller.filter.IFilterController;
import javafx.event.ActionEvent;

public class FilterDispatcher {

    private final IFilterController filterController = FilterController.getInstance();


    public void activatePipeline(ActionEvent event) {

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
}
