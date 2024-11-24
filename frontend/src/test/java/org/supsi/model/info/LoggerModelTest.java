package org.supsi.model.info;

import ch.supsi.application.translations.TranslationsApplication;
import javafx.collections.ObservableList;
import org.apache.commons.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoggerModelTest {

    @Mock
    private TranslationsApplication mockApp;

    private LoggerModel loggerModel;

    @BeforeEach
    void setUp() throws Exception {
        Field instance = LoggerModel.class.getDeclaredField("myself");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    void testSingletonInstance() {
        LoggerModel instance1 = LoggerModel.getInstance();
        LoggerModel instance2 = LoggerModel.getInstance();

        assertSame(instance1, instance2, "LoggerModel should be a singleton");
    }

    @Test
    void testAddInfoLog() {
        try(MockedStatic<TranslationsApplication> mockedStatic = mockStatic(TranslationsApplication.class)) {
            mockedStatic.when(TranslationsApplication::getInstance).thenReturn(mockApp);

            when(mockApp.translate("Test info log")).thenReturn(Optional.of("Test info log"));

            loggerModel = LoggerModel.getInstance();

            loggerModel.setShowInfo(true);

            loggerModel.addInfo("Test info log");

            ObservableList<LogEntry> logs = loggerModel.getLogs();
            assertEquals(1, logs.size());
            assertEquals(LogEntry.LogType.INFO, logs.get(0).type());
            assertEquals("Test info log", logs.get(0).message());

        }
    }

    @Test
    void testAddErrorLog() {
        try(MockedStatic<TranslationsApplication> mockedStatic = mockStatic(TranslationsApplication.class)) {
            mockedStatic.when(TranslationsApplication::getInstance).thenReturn(mockApp);

            when(mockApp.translate("Test error log")).thenReturn(Optional.of("Test error log"));
            loggerModel = LoggerModel.getInstance();
            loggerModel.setShowError(true);

            loggerModel.addError("Test error log");

            ObservableList<LogEntry> logs = loggerModel.getLogs();
            assertEquals(1, logs.size());
            assertEquals(LogEntry.LogType.ERROR, logs.get(0).type());
            assertEquals("Test error log", logs.get(0).message());
        }
    }

    @Test
    void testAddWarningLog() {
        try(MockedStatic<TranslationsApplication> mockedStatic = mockStatic(TranslationsApplication.class)) {
            mockedStatic.when(TranslationsApplication::getInstance).thenReturn(mockApp);

            when(mockApp.translate("Test warning log")).thenReturn(Optional.of("Test warning log"));
            loggerModel = LoggerModel.getInstance();

            loggerModel.setShowWarning(true);

            loggerModel.addWarning("Test warning log");

            ObservableList<LogEntry> logs = loggerModel.getLogs();
            assertEquals(1, logs.size());
            assertEquals(LogEntry.LogType.WARNING, logs.get(0).type());
            assertEquals("Test warning log", logs.get(0).message());
        }
    }

    @Test
    void testAddDebugLog() {
        try(MockedStatic<TranslationsApplication> mockedStatic = mockStatic(TranslationsApplication.class)) {
            mockedStatic.when(TranslationsApplication::getInstance).thenReturn(mockApp);

            when(mockApp.translate("Test debug log")).thenReturn(Optional.of("Test debug log"));
            loggerModel = LoggerModel.getInstance();
            loggerModel.setShowDebug(true);

            loggerModel.addDebug("Test debug log");

            ObservableList<LogEntry> logs = loggerModel.getLogs();
            assertEquals(1, logs.size());
            assertEquals(LogEntry.LogType.DEBUG, logs.get(0).type());
            assertEquals("Test debug log", logs.get(0).message());
        }
    }

    @Test
    void testLogNotAddedWhenTypeIsDisabledInfo() {
        loggerModel = LoggerModel.getInstance();

        loggerModel.setShowInfo(false);

        loggerModel.addInfo("This log should not appear");

        ObservableList<LogEntry> logs = loggerModel.getLogs();
        assertTrue(logs.isEmpty(), "Logs should be empty when the type is disabled");
    }

    @Test
    void testLogNotAddedWhenTypeIsDisabledWarning() {
        loggerModel = LoggerModel.getInstance();

        loggerModel.setShowWarning(false);

        loggerModel.addWarning("This log should not appear");

        ObservableList<LogEntry> logs = loggerModel.getLogs();
        assertTrue(logs.isEmpty(), "Logs should be empty when the type is disabled");
    }

    @Test
    void testLogNotAddedWhenTypeIsDisabledError() {
        loggerModel = LoggerModel.getInstance();

        loggerModel.setShowError(false);

        loggerModel.addError("This log should not appear");

        ObservableList<LogEntry> logs = loggerModel.getLogs();
        assertTrue(logs.isEmpty(), "Logs should be empty when the type is disabled");
    }

    @Test
    void testLogNotAddedWhenTypeIsDisabledDebug() {
        loggerModel = LoggerModel.getInstance();

        loggerModel.setShowDebug(false);

        loggerModel.addDebug("This log should not appear");

        ObservableList<LogEntry> logs = loggerModel.getLogs();
        assertTrue(logs.isEmpty(), "Logs should be empty when the type is disabled");
    }

    @Test
    void testClearLogs() {
        loggerModel = LoggerModel.getInstance();

        loggerModel.setShowInfo(true);
        loggerModel.addInfo("Log 1");
        loggerModel.addInfo("Log 2");

        loggerModel.clear();

        ObservableList<LogEntry> logs = loggerModel.getLogs();
        assertTrue(logs.isEmpty(), "Logs should be empty after clearing");
    }
}
