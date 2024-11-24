package org.supsi.model.info;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LogEntryTest {

    @Test
    void testLogEntryCreation() {
        LogEntry logEntry = new LogEntry(LogEntry.LogType.INFO, "This is an info message");
        assertEquals(LogEntry.LogType.INFO, logEntry.type());
        assertEquals("This is an info message", logEntry.message());
        assertEquals("#61addc", logEntry.getHexColor());
    }

    @Test
    void testLogEntryHexColorForInfo() {
        LogEntry.LogType type = LogEntry.LogType.INFO;
        assertEquals("#61addc", type.getHexColor());
    }

    @Test
    void testLogEntryHexColorForError() {
        LogEntry.LogType type = LogEntry.LogType.ERROR;
        assertEquals("#ed5c5c", type.getHexColor());
    }

    @Test
    void testLogEntryHexColorForWarning() {
        LogEntry.LogType type = LogEntry.LogType.WARNING;
        assertEquals("#d18f66", type.getHexColor());
    }

    @Test
    void testLogEntryHexColorForDebug() {
        LogEntry.LogType type = LogEntry.LogType.DEBUG;
        assertEquals("#6fd565", type.getHexColor());
    }

    @Test
    void testLogEntryError() {
        LogEntry errorLog = new LogEntry(LogEntry.LogType.ERROR, "This is an error message");
        assertEquals(LogEntry.LogType.ERROR, errorLog.type());
        assertEquals("This is an error message", errorLog.message());
        assertEquals("#ed5c5c", errorLog.getHexColor());
    }

    @Test
    void testLogEntryWarning() {
        LogEntry warningLog = new LogEntry(LogEntry.LogType.WARNING, "This is a warning message");
        assertEquals(LogEntry.LogType.WARNING, warningLog.type());
        assertEquals("This is a warning message", warningLog.message());
        assertEquals("#d18f66", warningLog.getHexColor());
    }

    @Test
    void testLogEntryInfo() {
        LogEntry warningLog = new LogEntry(LogEntry.LogType.INFO, "This is a info message");
        assertEquals(LogEntry.LogType.INFO, warningLog.type());
        assertEquals("This is a info message", warningLog.message());
        assertEquals("#61addc", warningLog.getHexColor());
    }
    @Test
    void testLogEntryDebug() {
        LogEntry warningLog = new LogEntry(LogEntry.LogType.DEBUG, "This is a debug message");
        assertEquals(LogEntry.LogType.DEBUG, warningLog.type());
        assertEquals("This is a debug message", warningLog.message());
        assertEquals("#6fd565", warningLog.getHexColor());
    }
}
