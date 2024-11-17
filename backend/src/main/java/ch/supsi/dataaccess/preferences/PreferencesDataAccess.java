package ch.supsi.dataaccess.preferences;

import ch.supsi.business.preferences.PreferencesDataAccessInterface;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * Provides access to user preferences, including loading, saving, and creating default preferences.
 * This class handles preferences stored in the user's filesystem and ensures that they are available
 * for the application runtime. It uses a singleton pattern to guarantee a single instance.
 */
public class PreferencesDataAccess implements PreferencesDataAccessInterface {

    /* Path to the default user preferences file */
    private static final String DEFAULT_USER_PREFERENCES_PROPERTIES = "/default-user-preferences.properties";
    /* The user's home directory */
    private static String USER_HOME_DIRECTORY;
    /* Directory to store preferences in the user's filesystem */
    private static final String PREFERENCES_DIRECTORY = ".imageEditor";
    /* Name of the preferences file in the user's filesystem */
    private static final String PREFERENCES_FILE = "/user-preferences.properties";
    /* Singleton instance */
    private static PreferencesDataAccess dao;
    /* In-memory representation of the user preferences modified at runtime */
    private Properties newProperties;

    /**
     * Protected default constructor to avoid instantiation from external clients.
     * Initializes the user's home directory.
     */
    protected PreferencesDataAccess() {
        USER_HOME_DIRECTORY = System.getProperty("user.home");
    }

    /**
     * Singleton method to retrieve the instance of PreferencesDataAccess.
     *
     * @return the singleton instance of {@link PreferencesDataAccess}
     */
    public static PreferencesDataAccess getInstance() {
        if (dao == null) {
            dao = new PreferencesDataAccess();
        }

        return dao;
    }

    /**
     * Builds the path to the preferences directory in the user's filesystem.
     *
     * @return the {@link Path} to the preferences directory
     */
    private Path getUserPreferencesDirectoryPath() {
        return Path.of(USER_HOME_DIRECTORY, PREFERENCES_DIRECTORY);
    }

    /**
     * Checks if the preferences directory exists in the user's filesystem.
     *
     * @return true if the directory exists, false otherwise
     */
    private boolean userPreferencesDirectoryExists() {
        return Files.exists(this.getUserPreferencesDirectoryPath());
    }

    /**
     * Creates the user preferences directory in the filesystem.
     *
     * @throws IOException if an error occurs while creating the directory
     */
    private void createUserPreferencesDirectory() throws IOException {
        try {
            Files.createDirectories(this.getUserPreferencesDirectoryPath());
        } catch (IOException e) {
            throw new IOException(String.format("Unable to create user preferences directory: %s\n", e.getMessage()));
        }
    }

    /**
     * Builds the path to the preferences file in the user's filesystem.
     *
     * @return the {@link Path} to the preferences file
     */
    private Path getUserPreferencesFilePath() {
        return Path.of(USER_HOME_DIRECTORY, PREFERENCES_DIRECTORY, PREFERENCES_FILE);
    }

    /**
     * Checks if the user preferences file exists in the filesystem.
     *
     * @return {@code true} if the file exists, {@code false} otherwise
     */
    private boolean userPreferencesFileExists() {
        return Files.exists(this.getUserPreferencesFilePath());
    }

    /**
     * Creates a user preferences file with the default preferences.
     *
     * @param defaultPreferences the default preferences to be stored in the file
     * @throws IOException if an error occurs while creating the file
     */
    private void createUserPreferencesFile(Properties defaultPreferences) throws IOException {
        if (!userPreferencesDirectoryExists()) {
            this.createUserPreferencesDirectory();
        }
        try {
            FileOutputStream outputStream = new FileOutputStream(String.valueOf(this.getUserPreferencesFilePath()));
            defaultPreferences.store(outputStream, null);
        } catch (IOException e) {
            throw new IOException(String.format("Unable to create user preferences file: %s\n", getUserPreferencesFilePath()));
        }
    }

    /**
     * Loads the default user preferences from the properties file.
     *
     * @return a {@link Properties} object containing the default preferences
     * @throws IOException if an error occurs while loading the default preferences
     */
    private  Properties loadDefaultPreferences() throws IOException {
        Properties defaultPreferences = new Properties();
        try {
            InputStream defaultPreferencesStream = getPreferencesResourceAsStream(DEFAULT_USER_PREFERENCES_PROPERTIES);
            defaultPreferences.load(defaultPreferencesStream);
        } catch (IOException e) {
            throw new IOException(String.format("Unable to load user preferences file: %s\n", getUserPreferencesFilePath()));
        }
        return defaultPreferences;
    }

    /**
     * Provides an InputStream for the given resource path. Can be overridden for testing purposes.
     *
     * @param resource the path to the resource
     * @return an {@link InputStream} for the resource
     */
    protected InputStream getPreferencesResourceAsStream(String resource) {
        return this.getClass().getResourceAsStream(resource);
    }

    /**
     * Loads and returns the preferences from the specified file path.
     *
     * @param path the path to the preferences file
     * @return a {@link Properties} object containing the loaded preferences
     * @throws IOException if an error occurs while loading the preferences
     */
    private Properties loadPreferences(Path path) throws IOException {
        Properties preferences = new Properties();
        try {
            preferences.load(Files.newInputStream(path));
        } catch (IOException e) {
            throw new IOException(String.format("Unable to load user preferences file from the specified path: %s\n", path));
        }
        return preferences;
    }

    /**
     * Retrieves the user's preferences. If no preferences file exists, a new one is created with default preferences.
     *
     * @return a {@link Properties} object containing the user's preferences
     */
    @Override
    public Properties getPreferences() {
        try {
            Properties userPreferences;
            if (userPreferencesFileExists()) {
                userPreferences = this.loadPreferences(this.getUserPreferencesFilePath());
                newProperties = (Properties) userPreferences.clone();
                return userPreferences;
            }

            userPreferences = this.loadDefaultPreferences();
            newProperties = (Properties) userPreferences.clone();
            createUserPreferencesFile(userPreferences);
            return userPreferences;
        } catch (IOException e) {
            return new Properties();
        }
    }

    /**
     * Stores a preference in the user preferences file.
     * Updates a copy of the user preferences and writes it to the filesystem.
     *
     * @param preference a key-value pair representing the preference to be stored
     * @return {@code true} if the operation succeeds, {@code false} false otherwise
     * @throws IOException if an error occurs while storing the preference
     */
    @Override
    public boolean storePreference(Map.Entry<String, String> preference) throws IOException {
        if (newProperties == null) {
            newProperties = getPreferences();
        }

        newProperties.setProperty(preference.getKey(), preference.getValue());

        try {
            OutputStream outputStream = Files.newOutputStream(this.getUserPreferencesFilePath());
            newProperties.store(outputStream, null);
            outputStream.close();
        } catch (IOException e) {
            throw new IOException(String.format("Unable to store preference: %s\n", getUserPreferencesFilePath()), e);
        }
        return true;
    }
}
