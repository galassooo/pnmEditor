package ch.supsi.application.preferences;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PreferencesApplicationTest {

    private static PreferencesApplication preferencesApplication;

    @TempDir
    static Path tempDir;

    @BeforeAll
    static void setUp() throws IOException {
        // Crea una directory unica per i test
        Path uniqueTestDir = tempDir.resolve("test-" + System.nanoTime());
        System.setProperty("user.home", uniqueTestDir.toAbsolutePath().toString());

        // Scrivi un file di preferenze iniziali
        Path preferencesDir = uniqueTestDir.resolve(".imageEditor");
        Path preferencesFile = preferencesDir.resolve("user-preferences.properties");

        Files.createDirectories(preferencesDir);
        Files.writeString(preferencesFile, "language-tag=en-US\nfont-size=12");

        // Inizializza l'applicazione
        preferencesApplication = PreferencesApplication.getInstance();
    }

    @AfterAll
    static void tearDown() {
        // Ripristina la proprietà di sistema
        System.clearProperty("user.home");
    }

    @Test
    void testGetPreferenceExistingKey() {
        Optional<Object> languageTag = preferencesApplication.getPreference("language-tag");
        assertTrue(languageTag.isPresent(), "The language-tag should be present");
        assertEquals("en-US", languageTag.get(), "Expected the language-tag to be 'en-US'");
    }

    @Test
    void testGetPreferenceNonExistentKey() {
        Optional<Object> nonExistent = preferencesApplication.getPreference("non-existent-key");
        assertFalse(nonExistent.isPresent(), "The non-existent key should return an empty Optional");
    }

    @Test
    void testSetPreferenceNewPreference() throws IOException {
        Map.Entry<String, String> newPreference = new AbstractMap.SimpleEntry<>("theme", "dark");
        preferencesApplication.setPreference(newPreference);

        // Verifica che il valore sia stato salvato
        Optional<Object> theme = preferencesApplication.getPreference("theme");
        assertTrue(theme.isPresent(), "The theme key should be present");
        assertEquals("dark", theme.get(), "The theme should be stored as 'dark'");
    }

    @Test
    void testSetPreferenceUpdateExistingPreference() throws IOException {
        Map.Entry<String, String> updatedPreference = new AbstractMap.SimpleEntry<>("font-size", "14");
        preferencesApplication.setPreference(updatedPreference);

        // Verifica che il valore sia stato aggiornato
        Optional<Object> fontSize = preferencesApplication.getPreference("font-size");
        assertTrue(fontSize.isPresent(), "The font-size key should be present");
        assertEquals("14", fontSize.get(), "The font-size should be updated to '14'");
    }

    @Test
    void testSingleton() {
        PreferencesApplication instance = PreferencesApplication.getInstance();
        assertSame(preferencesApplication, instance, "The same instance should be returned for Singleton");
    }
}
