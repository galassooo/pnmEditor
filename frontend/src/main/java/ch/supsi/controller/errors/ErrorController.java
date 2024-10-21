package ch.supsi.controller.errors;

import ch.supsi.model.errors.ErrorModel;
import ch.supsi.model.errors.IErrorModel;
import ch.supsi.view.info.IErrorPopUp;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class ErrorController implements IErrorController {
    private final IErrorModel model = ErrorModel.getInstance();

    private IErrorPopUp errorPopUp;

    private static ErrorController mySelf;

    private Stage stage;

    public static ErrorController getInstance() {
        if (mySelf == null) {
            mySelf = new ErrorController();
        }
        return mySelf;
    }

    private ErrorController() {
        URL fxmlUrl = getClass().getResource("/layout/ErrorPopUp.fxml");
        if (fxmlUrl == null) {
            return;
        }
        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        try {
            loader.load();
            errorPopUp = loader.getController();
            errorPopUp.setModel(model);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void showError(String message){
        //va aggiunto il logger, stampa il log di debug + error + info su richiesta
        model.setMessage(message);
        errorPopUp.build();
        errorPopUp.show();
    }
}
