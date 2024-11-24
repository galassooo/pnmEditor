package org.supsi.controller.exit;


import javafx.application.Platform;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.supsi.controller.confirmation.ConfirmationController;
import org.supsi.controller.confirmation.IConfirmationController;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExitControllerTest {

    @Mock
    private ConfirmationController mockConfirmationController;

    @BeforeEach
    public void setUp() throws Exception {
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


    @Disabled
    @Test
    void testHandleExitWithoutStage() throws Exception {
        try (MockedStatic<ConfirmationController> confirmationControllerMock = mockStatic(ConfirmationController.class);
             MockedStatic<Platform> platformMock = mockStatic(Platform.class);
             MockedStatic<System> systemMock = mockStatic(System.class);) {


            confirmationControllerMock.when(ConfirmationController::getInstance).thenReturn(mockConfirmationController);

            ExitController controller = ExitController.getInstance();


            controller.handleExit(null);
            verify(mockConfirmationController).requestConfirm(any());


            controller.handleExit(null);

            platformMock.verify(Platform::exit);
        }
    }
}
