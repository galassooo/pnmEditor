package ch.supsi.dataaccess.preferences;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PreferencesDataAccessTest {

    private PreferencesDataAccess dataAccess;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws Exception {
        resetSingleton();

        //override the user.home property to make it a unique directory for each test
        Path uniqueTestDir = tempDir.resolve("test-" + System.nanoTime());
        System.setProperty("user.home", uniqueTestDir.toAbsolutePath().toString());

        /*
        forzo la creazione di una nuova istanza col costruttore a protected per ogni test
        serve a creare un ambiente isolato
        */
        Constructor<PreferencesDataAccess> constructor = PreferencesDataAccess.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        dataAccess = constructor.newInstance();

    }

    @AfterEach
    void tearDown() {
        System.clearProperty("user.home"); //Clear property
    }

    private void resetSingleton() throws Exception {
        //uses reflections to clear singleton class

        //NON MI PIACE UN GRANCHE MA NON HO TROVATO NESSUN ALTRO MODO
        //DATO CHE UN'ISTANZA CONDIVISA PER TUTTI I TEST NON PERMETTEVA
        //DI ESEGUIRE I TEST IN AMBIENTI ISOLATI
        Field instance = PreferencesDataAccess.class.getDeclaredField("dao");
        instance.setAccessible(true);
        instance.set(null, null);


    }

    /* --------------- singleton --------------- */

    @Test
    void testSingleton(){
        PreferencesDataAccess one = PreferencesDataAccess.getInstance();
        PreferencesDataAccess two = PreferencesDataAccess.getInstance();
        assertSame(one, two);
    }

    /* --------------- valid input --------------- */
    @Test
    void testGetPreferences() throws IOException {
        Properties preferences = dataAccess.getPreferences();
        assertNotNull(preferences);
    }

    @Test
    void testStorePreferencesAfterGet() throws IOException {

        Properties preferences = dataAccess.getPreferences();
        Map.Entry<String, String> preference = Map.entry("theme", "dark");
        assertTrue(dataAccess.storePreference(preference));
        assertNotNull(preferences);

    }

    @Test
    void storePreference() throws IOException {
        Map.Entry<String, String> preference = Map.entry("theme", "dark");
        boolean success = dataAccess.storePreference(preference);
        assertTrue(success);

    }

    @Test
    void testLoadExistingPreferences() throws IOException {
        Path preferencesDirPath = Path.of(System.getProperty("user.home"), ".imageEditor");
        Path preferencesFile = Path.of(preferencesDirPath.toString(), "user-preferences.properties");
        Files.createDirectories(preferencesDirPath);
        Files.writeString(preferencesFile, "ciao=test");

        assertDoesNotThrow(() -> {dataAccess.getPreferences();});
        assertEquals(dataAccess.getPreferences().getProperty("ciao"), "test");
    }

    @Test
    void testExistingDirButMissingFile() throws IOException {
        Path preferencesDirPath = Path.of(System.getProperty("user.home"), ".imageEditor");
        Files.createDirectories(preferencesDirPath);
        assertDoesNotThrow(() -> {dataAccess.getPreferences();});
    }

    /* --------------- exceptions --------------- */
    @Test
    void testStorePreferenceIOException() throws IOException {
        dataAccess.getPreferences();

        //simulate IOException when the file is loaded
        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {

            mockedFiles.when(() -> Files.exists(any(Path.class))).thenReturn(true);
            mockedFiles.when(() -> Files.newOutputStream(any(Path.class)))
                    .thenThrow(new IOException("Simulated IOException"));

            Map.Entry<String, String> preference = Map.entry("theme", "dark");

            IOException exception = assertThrows(IOException.class, () -> dataAccess.storePreference(preference));
            System.out.println(exception.getMessage());
            assertTrue(exception.getMessage().contains("Unable to store preference"));

            mockedFiles.verify(() -> Files.newOutputStream(any(Path.class)), times(1));
        }
    }

    @Test
    void testIOExceptionOnCreatePreferencesDirectory() {
        Path preferencesDirPath = Path.of(System.getProperty("user.home"), ".imageEditor");

        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
            //simulate missing directory and throws an exception while the dao tries to create it
            mockedFiles.when(() -> Files.exists(preferencesDirPath)).thenReturn(false);
            mockedFiles.when(() -> Files.createDirectories(preferencesDirPath))
                    .thenThrow(new IOException("Simulated IOException"));

            assertDoesNotThrow(() -> dataAccess.getPreferences());
        }
    }


    @Test
    void testIOExceptionOnLoadPreferencesFile() {
        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {

            //creates an IOException when the file is loaded
            mockedFiles.when(() -> Files.exists(any(Path.class))).thenReturn(true);
            mockedFiles.when(() -> Files.newInputStream(any(Path.class)))
                    .thenThrow(new IOException("Simulated IOException on load"));

            assertDoesNotThrow( () -> dataAccess.getPreferences());

            mockedFiles.verify(() -> Files.newInputStream(any(Path.class)), times(1));
        }
    }


    @Test
    void testLoadDefaultPreferencesIOExceptionDuringLoad() throws IOException {
        //create a spy object
        Path preferencesFilePath = Paths.get(System.getProperty("user.home"), ".imageEditor", "user-preferences.properties");
        Files.deleteIfExists(preferencesFilePath);
        PreferencesDataAccess dataAccessSpy = spy(new PreferencesDataAccess());

        //input stream mock to generate exception
        InputStream failingInputStream = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("Simulated IOException during load");
            }
        };

        //configure wrapper object to use the mocked input stream
        doReturn(failingInputStream).when(dataAccessSpy).getPreferencesResourceAsStream("/default-user-preferences.properties");

        //execute test
        assertDoesNotThrow(dataAccessSpy::getPreferences);
    }

    @Test
    void testIOExceptionOnCreatingUserPreferencesFile() {

        //mocks file os to throw an exception
        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.exists(any(Path.class))).thenReturn(false);
            mockedFiles.when(() -> Files.createDirectories(any(Path.class))).thenReturn(tempDir);
            mockedFiles.when(() -> Files.newOutputStream(any(Path.class)))
                    .thenThrow(new IOException("Simulated IOException in output stream"));

            //verify exception
            assertDoesNotThrow(() -> {
                dataAccess.getPreferences();
            });
        }
    }

    @Test
    void testGetPreferencesResourceAsStreamReturnsEmptyStream() throws IOException {
        //create a spy instance
        PreferencesDataAccess dataAccessSpy = spy(new PreferencesDataAccess());

        //override inputstream to return an empty object
        doReturn(new ByteArrayInputStream(new byte[0]))
                .when(dataAccessSpy)
                .getPreferencesResourceAsStream("/default-user-preferences.properties");

        //verify that preferences are EMPTY (not null)
        Properties preferences = dataAccessSpy.getPreferences();
        assertTrue(preferences.isEmpty(), "Preferences should be empty");
    }
}
