package org.supsi.controller.errors;

import org.supsi.model.errors.ErrorModel;
import org.supsi.model.errors.IErrorModel;
import org.supsi.model.info.ILoggerModel;
import org.supsi.model.info.LoggerModel;
import org.supsi.model.translations.ITranslationsModel;
import org.supsi.model.translations.TranslationModel;
import org.supsi.view.info.IErrorPopUp;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class ErrorController implements IErrorController {
    private final IErrorModel model = ErrorModel.getInstance();
    private final ITranslationsModel translationsModel = TranslationModel.getInstance();
    private final ILoggerModel loggerModel = LoggerModel.getInstance();

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

        try {
            FXMLLoader loader = new FXMLLoader(fxmlUrl,translationsModel.getUiBundle());
            loader.load();
            errorPopUp = loader.getController();
            errorPopUp.setModel(model);

        } catch (IOException e) {
            loggerModel.addError("ui_failed_to_load_component");
        }
        loggerModel.addDebug("ui_help_loaded");
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void showError(String message){
        //va aggiunto il logger, stampa il log di debug + error + info su richiesta
        loggerModel.addError(message);
        model.setMessage(message);
        loggerModel.addDebug("ui_start_popup_build");
        errorPopUp.build();
        loggerModel.addDebug("ui_end_popup_build");
        errorPopUp.show();
        loggerModel.addDebug("ui_popup_show");
    }
}
