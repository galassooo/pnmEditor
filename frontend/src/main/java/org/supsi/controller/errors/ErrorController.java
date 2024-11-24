package org.supsi.controller.errors;

import org.supsi.model.errors.ErrorModel;
import org.supsi.model.errors.IErrorModel;
import org.supsi.model.info.ILoggerModel;
import org.supsi.model.info.LoggerModel;
import org.supsi.model.translations.ITranslationsModel;
import org.supsi.model.translations.TranslationModel;
import org.supsi.view.IView;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import java.net.URL;


/**
 * Controller responsible for handling error messages and displaying them to the user through a popup view.
 * Uses the {@link IErrorModel} to manage error data and the {@link IView} interface to render the popup.
 */
public class ErrorController implements IErrorController {

    private static ErrorController mySelf;
    private final IErrorModel model;
    private final ILoggerModel loggerModel;
    private IView<IErrorModel> errorPopUp;

    /**
     * Private constructor to implement the Singleton pattern.
     * Loads the error popup view and sets its model.
     */
    protected ErrorController() {
        model  = ErrorModel.getInstance();
        loggerModel = LoggerModel.getInstance();

        URL fxmlUrl = getResource("/layout/ErrorPopUp.fxml");
        if (fxmlUrl == null) {
            return;
        }

        try {
            ITranslationsModel translationsModel = TranslationModel.getInstance();
            FXMLLoader loader = new FXMLLoader(fxmlUrl, translationsModel.getUiBundle());
            loader.load();
            errorPopUp = loader.getController();
            errorPopUp.setModel(model);

        } catch (IOException e) {
            loggerModel.addError("ui_failed_to_load_component");
        }
        loggerModel.addDebug("ui_error_loaded");
    }

    /**
     * Retrieves the singleton instance of the {@code ErrorController}.
     *
     * @return The singleton instance.
     */
    public static ErrorController getInstance() {
        if (mySelf == null) {
            mySelf = new ErrorController();
        }
        return mySelf;
    }

    protected URL getResource(String path){
        return getClass().getResource(path);
    }

    /**
     * {@inheritDoc}
     * @param message The error message to be shown.
     */
    @Override
    public void showError(String message){
        loggerModel.addError(message);
        model.setMessage(message);
        loggerModel.addDebug("ui_start_popup_build");
        errorPopUp.build();
        loggerModel.addDebug("ui_end_popup_build");
        errorPopUp.show();
        loggerModel.addDebug("ui_popup_show");
    }
}
