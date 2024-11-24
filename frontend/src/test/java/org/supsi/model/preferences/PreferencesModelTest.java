package org.supsi.model.preferences;

import ch.supsi.application.preferences.PreferencesApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.AbstractMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PreferencesModelTest {

    @Mock
    private PreferencesApplication mockApp;

    private PreferencesModel prefModel;

    @BeforeEach
    public void setUp() throws Exception {
        Field field = PreferencesModel.class.getDeclaredField("myself");
        field.setAccessible(true);
        field.set(null, null);
    }

    @Test
    void testSingleton(){
        assertEquals(PreferencesModel.getInstance(), PreferencesModel.getInstance());
    }

    @Test
    void testGetPreferences(){

        try(MockedStatic<PreferencesApplication> staticMock = mockStatic(PreferencesApplication.class)) {
            staticMock.when(PreferencesApplication::getInstance).thenReturn(mockApp);

            when(mockApp.getPreference("testKey")).thenReturn(Optional.of("testValue"));

            prefModel = PreferencesModel.getInstance();

            assertTrue(prefModel.getPreference("testKey").isPresent());
            assertEquals("testValue", prefModel.getPreference("testKey").get());
        }
    }

    @Test
    void testSetPreferenceNoException() throws IOException {

        try(MockedStatic<PreferencesApplication> staticMock = mockStatic(PreferencesApplication.class)) {
            staticMock.when(PreferencesApplication::getInstance).thenReturn(mockApp);

            doNothing().when(mockApp).setPreference(new AbstractMap.SimpleEntry<>("a", "b"));

            prefModel = PreferencesModel.getInstance();

            assertDoesNotThrow(() -> prefModel.setPreference("a", "b"));
        }
    }

    @Test
    void testSetPreferenceIOException() throws IOException {

        try(MockedStatic<PreferencesApplication> staticMock = mockStatic(PreferencesApplication.class)) {
            staticMock.when(PreferencesApplication::getInstance).thenReturn(mockApp);

            doThrow(IOException.class).when(mockApp).setPreference(new AbstractMap.SimpleEntry<>("a", "b"));

            prefModel = PreferencesModel.getInstance();

            assertThrows(IOException.class, () -> prefModel.setPreference("a", "b"));
        }
    }
}
