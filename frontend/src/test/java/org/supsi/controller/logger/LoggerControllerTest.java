package org.supsi.controller.logger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.supsi.model.info.LoggerModel;
import org.supsi.model.preferences.PreferencesModel;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoggerControllerTest {

    @Mock
    private LoggerModel mockLoggerModel;

    @Mock
    private PreferencesModel mockPreferencesModel;

    @BeforeEach
    void setUp() throws Exception {

        var field = LoggerController.class.getDeclaredField("myself");
        field.setAccessible(true);
        field.set(null, null);
        MockitoAnnotations.openMocks(this);

        try (MockedStatic<LoggerModel> loggerModelMockedStatic = mockStatic(LoggerModel.class);
             MockedStatic<PreferencesModel> preferencesModelMockedStatic = mockStatic(PreferencesModel.class)) {

            loggerModelMockedStatic.when(LoggerModel::getInstance).thenReturn(mockLoggerModel);
            preferencesModelMockedStatic.when(PreferencesModel::getInstance).thenReturn(mockPreferencesModel);

        }
    }

    @Test
    void testSingletonBehavior() {
        LoggerController controller1 = LoggerController.getInstance();
        LoggerController controller2 = LoggerController.getInstance();

        assertEquals(controller1, controller2, "LoggerController should be a singleton");
    }

    @Test
    void testLoadProperties() {
        when(mockPreferencesModel.getPreference("show-debug")).thenReturn(Optional.of(true));
        when(mockPreferencesModel.getPreference("show-info")).thenReturn(Optional.of(false));
        when(mockPreferencesModel.getPreference("show-warning")).thenReturn(Optional.of(true));
        when(mockPreferencesModel.getPreference("show-error")).thenReturn(Optional.of(false));

        try (MockedStatic<LoggerModel> loggerModelMockedStatic = mockStatic(LoggerModel.class);
             MockedStatic<PreferencesModel> preferencesModelMockedStatic = mockStatic(PreferencesModel.class)) {

            loggerModelMockedStatic.when(LoggerModel::getInstance).thenReturn(mockLoggerModel);
            preferencesModelMockedStatic.when(PreferencesModel::getInstance).thenReturn(mockPreferencesModel);

            LoggerController.getInstance();
        }

        verify(mockLoggerModel).setShowDebug(true);
        verify(mockLoggerModel).setShowInfo(false);
        verify(mockLoggerModel).setShowWarning(true);
        verify(mockLoggerModel).setShowError(false);
    }

    @Test
    void testLoadPropertiesWithDefaults() {

        try (MockedStatic<LoggerModel> loggerModelMockedStatic = mockStatic(LoggerModel.class);
             MockedStatic<PreferencesModel> preferencesModelMockedStatic = mockStatic(PreferencesModel.class)) {

            loggerModelMockedStatic.when(LoggerModel::getInstance).thenReturn(mockLoggerModel);
            preferencesModelMockedStatic.when(PreferencesModel::getInstance).thenReturn(mockPreferencesModel);

            LoggerController.getInstance();
        }

        verify(mockLoggerModel).setShowDebug(false);
        verify(mockLoggerModel).setShowInfo(false);
        verify(mockLoggerModel).setShowWarning(false);
        verify(mockLoggerModel).setShowError(false);
    }

    @Test
    void testAddDebug() {
        LoggerController controller = LoggerController.getInstance();
        String debugMessage = "This is a debug message";
        assertDoesNotThrow(()->controller.addDebug(debugMessage));
    }
}
