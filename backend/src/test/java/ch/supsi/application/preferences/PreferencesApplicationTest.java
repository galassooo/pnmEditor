package ch.supsi.application.preferences;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PreferencesApplicationTest {

    private static PreferencesApplication preferencesApplication;

    @TempDir
    private static Path tempDir;

    @BeforeAll
    static void setUp() throws IOException {
        // Override the user.home property to create a unique directory for each test
        Path uniqueTestDir = tempDir.resolve("test-" + System.nanoTime());
        System.setProperty("user.home", uniqueTestDir.toAbsolutePath().toString());

        // Create default preference file with some values
        Path preferencesDir = uniqueTestDir.resolve(".imageEditor");
        Path preferencesFile = preferencesDir.resolve("user-preferences.properties");

        Files.createDirectories(preferencesDir);
        Files.writeString(preferencesFile, "language-tag=en-US\nfont-size=12");

        // Initialize the PreferencesApplication instance
        preferencesApplication = PreferencesApplication.getInstance();
    }

    @AfterAll
    static void  tearDown() {
        // Clear the overridden user.home property
        System.clearProperty("user.home");
    }

    @Test
    void testGetPreferenceExistingKey() throws IOException {
        // Retrieve an existing preference key
        Object languageTag = preferencesApplication.getPreference("language-tag");
        assertEquals("en-US", languageTag, "Expected the language-tag to be 'en-US'");
    }

    @Test
    void testGetPreferenceNonExistentKey() throws IOException {
        // Try to retrieve a non-existent preference key
        Object nonExistent = preferencesApplication.getPreference("non-existent-key");
        assertNull(nonExistent, "Expected null for a non-existent key");
    }

    @Test
    void testSetPreferenceNewPreference() throws IOException {
        // Set a new preference and verify it's stored
        Map.Entry<String, String> newPreference = new AbstractMap.SimpleEntry<>("theme", "dark");
        preferencesApplication.setPreference(newPreference);

        // Retrieve the new preference to verify it was saved correctly
        Object theme = preferencesApplication.getPreference("theme");
        assertEquals("dark", theme, "The theme should be stored as 'dark'");
    }

    @Test
    void testSetPreferenceUpdateExistingPreference() throws IOException {
        // Update an existing preference
        Map.Entry<String, String> updatedPreference = new AbstractMap.SimpleEntry<>("font-size", "14");
        preferencesApplication.setPreference(updatedPreference);

        // Retrieve the updated preference to verify the new value
        Object fontSize = preferencesApplication.getPreference("font-size");
        assertEquals("14", fontSize, "The font-size should be updated to '14'");
    }

    @Test
    void testSingleton(){
        assertEquals(preferencesApplication, PreferencesApplication.getInstance());
    }
}
