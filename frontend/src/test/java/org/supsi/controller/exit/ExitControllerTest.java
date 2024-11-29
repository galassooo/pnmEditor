package org.supsi.controller.exit;


import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.mockito.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.supsi.controller.confirmation.ConfirmationController;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExitControllerTest {

    @Mock
    private ConfirmationController mockConfirmationController;

    @Captor
    private ArgumentCaptor<Consumer<?>> runnableCaptor;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        var field = ExitController.class.getDeclaredField("instance");
        field.setAccessible(true);
        field.set(null, null);
    }

    @Test
    void testSingleton(){
        try(MockedStatic<ConfirmationController> staticMock = mockStatic(ConfirmationController.class)){

            staticMock.when(ConfirmationController::getInstance).thenReturn(mockConfirmationController);

            ExitController exitController = ExitController.getInstance();
            ExitController exitController1 = ExitController.getInstance();

            assertEquals(exitController, exitController1);
        }
    }

    @Test
    void testClose(){
        try(MockedStatic<ConfirmationController> staticMock = mockStatic(ConfirmationController.class)){

            staticMock.when(ConfirmationController::getInstance).thenReturn(mockConfirmationController);

            ExitController exitController = ExitController.getInstance();
            ExitController exitController1 = ExitController.getInstance();

            assertEquals(exitController, exitController1);
        }

    }


    @Test
    void testHandleExitWithoutStage() throws Exception {
        try (MockedStatic<ConfirmationController> confirmationControllerMock = mockStatic(ConfirmationController.class);) {

            confirmationControllerMock.when(ConfirmationController::getInstance).thenReturn(mockConfirmationController);

            ExitController controller = ExitController.getInstance();

            controller.handleExit(null);

            verify(mockConfirmationController).requestConfirm(runnableCaptor.capture());

            Consumer<?> capturedRunnable = runnableCaptor.getValue();
            capturedRunnable.accept(null);

        }
    }


    @Test
    void testHandleExitWithStage() throws Exception {
        try (MockedStatic<ConfirmationController> confirmationControllerMock = mockStatic(ConfirmationController.class);) {

            confirmationControllerMock.when(ConfirmationController::getInstance).thenReturn(mockConfirmationController);

            Stage mockedStage = mock(Stage.class);

            ExitController controller = ExitController.getInstance();
            controller.handleExit(mockedStage);

            verify(mockConfirmationController).requestConfirm(runnableCaptor.capture());

            Consumer<?> capturedRunnable = runnableCaptor.getValue();
            capturedRunnable.accept(null);

            verify(mockedStage).close();
        }
    }
}
