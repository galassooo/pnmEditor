package org.supsi.controller.confirmation;

import java.util.function.Consumer;

/**
 * Defines the behaviour that a generic exit controller should expose to the dispatcher
 */
public interface IConfirmationController {
    void requestConfirm(Consumer<?> onConfirm);
}