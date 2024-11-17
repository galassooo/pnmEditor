package org.supsi.controller.exit;

import org.supsi.controller.confirmation.ConfirmationController;
import org.supsi.controller.confirmation.IConfirmationController;
import javafx.stage.Stage;
import javafx.application.Platform;

public class ExitController implements IExitController{

    //*NO* EXTENDS CONFIRMATIONCONTROLLER!!!
    // viola liskov -> aggiunge responsabilità completamente diversa dal padre
    // viola SRP -> si occupa sia di conferma che di uscita
    //dunque per rispettare i SOLID è giusto che sia una classe a parte

    //OKAY 'has a' in quanto *USA* il confirmation ma NON LO DEVE ESTENDERE!!

    private static ExitController instance;
    private final IConfirmationController confirmationController;

    protected ExitController() {
        this.confirmationController = ConfirmationController.getInstance();
    }

    public static ExitController getInstance() {
        if (instance == null) {
            instance = new ExitController();
        }
        return instance;
    }

    @Override
    public void handleExit(Stage stage) {
        confirmationController.requestConfirm(result -> {
            if (stage != null) {
                stage.close();
            }
            Platform.exit();
            System.exit(0);
        });
    }
}