package org.supsi.controller.confirmation;

import javafx.fxml.FXMLLoader;
import org.supsi.model.info.ILoggerModel;
import org.supsi.model.info.LoggerModel;
import org.supsi.model.state.IStateModel;
import org.supsi.model.state.StateModel;
import org.supsi.model.translations.ITranslationsModel;
import org.supsi.model.translations.TranslationModel;
import org.supsi.view.info.IConfirmPopup;

import java.io.IOException;
import java.net.URL;
import java.util.function.Consumer;

public class ConfirmationController implements IConfirmationController {

    private static ConfirmationController instance;
    private IConfirmPopup view;
    private boolean changesPending = false;

    protected ConfirmationController() {

        IStateModel stateModel = StateModel.getInstance();
        stateModel.areChangesPending().addListener((obs, old, newValue) -> changesPending = newValue);

        URL fxmlUrl = getClass().getResource("/layout/ConfirmPopup.fxml");
        if (fxmlUrl == null) {
            return;
        }

        ILoggerModel loggerModel = LoggerModel.getInstance();
        try {
            ITranslationsModel translationsModel = TranslationModel.getInstance();
            FXMLLoader loader = new FXMLLoader(fxmlUrl, translationsModel.getUiBundle());
            loader.load();
            view = loader.getController();

        } catch (IOException e) {
            loggerModel.addError("ui_failed_to_load_component");
        }
        loggerModel.addDebug("ui__loaded");
    }

    /**
     *
     * @return an instance of this class
     */
    public static ConfirmationController getInstance() {
        if (instance == null) {
            instance = new ConfirmationController();
        }
        return instance;
    }

    /**
     * handle exit request
     */
    @Override
    public void requestConfirm(Consumer<?> onConfirm) {
        if(changesPending) {
            if (view.showConfirmationDialog()) {
                onConfirm.accept(null);
            }
        }else {
            onConfirm.accept(null);
        }
    }
}
