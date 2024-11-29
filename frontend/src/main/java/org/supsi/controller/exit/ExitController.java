package org.supsi.controller.exit;

import org.supsi.controller.confirmation.ConfirmationController;
import org.supsi.controller.confirmation.IConfirmationController;
import javafx.stage.Stage;
import javafx.application.Platform;
/**
 * The `ExitController` manages the application exit process, ensuring proper user confirmation
 * before shutting down the application. It uses an instance of {@link ConfirmationController}
 * to handle confirmation dialogs.
 */
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

    /**
     * Returns the Singleton instance of the {@link ExitController}
     *
     * @return The singleton instance.
     */
    public static ExitController getInstance() {
        if (instance == null) {
            instance = new ExitController();
        }
        return instance;
    }

    /**
     * Handles the application exit process. Requests user confirmation before closing the stage,
     * exiting the JavaFX platform, and terminating the JVM process.
     *
     * @param stage The primary {@link Stage} of the application to be closed. Can be null if no specific stage is involved.
     */
    @Override
    public void handleExit(Stage stage) {
        confirmationController.requestConfirm(result -> {
            if (stage != null) {
                stage.close();
            }
        });
    }
}