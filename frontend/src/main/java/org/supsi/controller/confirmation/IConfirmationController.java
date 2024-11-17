package org.supsi.controller.confirmation;

import java.util.function.Consumer;

/**
 * Defines the behaviour that a generic confirmation controller should expose
 */
public interface IConfirmationController {

    /**
     * Requests a confirmation from the user.
     *
     * @param onConfirm A {@link Consumer} that will be executed if the user confirms the action.
     */
    void requestConfirm(Consumer<?> onConfirm);
}