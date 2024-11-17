package ch.supsi.business.preferences;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PreferencesBusinessTest {

    private static PreferencesBusiness preferencesBusiness;

    @TempDir
    private static Path tempDir;

    @BeforeAll
    static void setUp() throws IOException {

        //override the user.home property to make it a unique directory for each test
        Path uniqueTestDir = tempDir.resolve("test-" + System.nanoTime());
        System.setProperty("user.home", uniqueTestDir.toAbsolutePath().toString());
        Path preferencesDirPath = Path.of(System.getProperty("user.home"), ".imageEditor");
        Path preferencesFile = Path.of(preferencesDirPath.toString(), "user-preferences.properties");
        Files.createDirectories(preferencesDirPath);
        Files.writeString(preferencesFile, "language-tag=en-US");
    }

    @AfterAll
    static void tearDown() {
        System.clearProperty("user.home"); //Clear property
    }

    @BeforeEach
    void setup() throws NoSuchFieldException, IllegalAccessException {
        preferencesBusiness = PreferencesBusiness.getInstance();
        //force reset `userPreferences` field in PreferencesBusiness to `null`
        Field userPreferencesField = PreferencesBusiness.class.getDeclaredField("userPreferences");
        userPreferencesField.setAccessible(true);
        userPreferencesField.set(preferencesBusiness, null);
    }

    /* --------------- singleton --------------- */
    @Test
    void testSingletonInstance() {
        PreferencesBusiness instance1 = PreferencesBusiness.getInstance();
        PreferencesBusiness instance2 = PreferencesBusiness.getInstance();

        assertSame(instance1, instance2, "PreferencesBusiness should be a singleton");
    }

    /* --------------- Language --------------- */
    @Test
    void testGetCurrentLanguage() throws IOException {
        //String currentLanguage = preferencesBusiness.getCurrentLanguage();

        //assertNotNull(currentLanguage, "Current language should be set in default preferences");
    }

    @Test
    void testDoubleGetCurrentLanguage() throws IOException {

        //String currentLanguage = preferencesBusiness.getCurrentLanguage();
        //String currentLanguage2 = preferencesBusiness.getCurrentLanguage();

        //assertNotNull(currentLanguage, "Current language should be set in default preferences");
        //assertNotNull(currentLanguage2, "Current language should be set in default preferences");
    }

    @Test
    void testSetLanguageTag() throws IOException {
        String newLanguageTag = "it-IT";

        preferencesBusiness.setCurrentLanguage(newLanguageTag);

        assertEquals(newLanguageTag, preferencesBusiness.getCurrentLanguage());
    }

    /* --------------- Preferences --------------- */
    @Test
    void testGetPreferenceExistingKey() throws IOException {

        preferencesBusiness.setPreference(Map.entry("theme", "dark"));

        Object theme = preferencesBusiness.getPreference("theme");

        assertEquals("dark", theme, "The theme should be 'dark' after setting it");
    }


    @Test
    void testGetPreferenceNonExistingKey() throws IOException {
        //try to load a non-existing key
        Object nonExistentKey = preferencesBusiness.getPreference("nonexistent");

        assertNull(nonExistentKey, "Requesting a non-existent key should return null");
    }

    @Test
    void testGetPreferenceNullKey() throws IOException {
        //retrieve with null key
        Object result = preferencesBusiness.getPreference(null);

        assertNull(result, "Null key should return null without error");
    }

    @Test
    void testGetPreferenceEmptyKey() throws IOException {
        //retrieve with empty string key
        Object result = preferencesBusiness.getPreference("");

        assertNull(result, "Empty key should return null without error");
    }

    @Test
    void testSetPreferenceNewPreference() throws IOException {
        //set a new preference
        preferencesBusiness.setPreference(Map.entry("fontSize", "12"));

        //verify it's saved and retrievable
        Object fontSize = preferencesBusiness.getPreference("fontSize");
        System.out.println(fontSize.toString());
        assertEquals("12", fontSize, "The font size should be stored as '12'");
    }
}
