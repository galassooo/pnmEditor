package ch.supsi.view.info;

import ch.supsi.model.errors.IErrorModel;
import javafx.stage.Stage;

public interface IErrorPopUp {

    void build();
    void show();
    void setModel(IErrorModel model);
}
