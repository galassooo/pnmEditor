package ch.supsi.dataaccess.preferences;

import ch.supsi.business.preferences.PreferencesDataAccessInterface;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;

public class PreferencesDataAccess implements PreferencesDataAccessInterface {

    /* where the default user preferences are located */
    private static final String DEFAULT_USER_PREFERENCES_PROPERTIES = "/default-user-preferences.properties";
    /* the user home directory */
    private static String USER_HOME_DIRECTORY;
    /* the default directory that will be created on the user's filesystem */
    private static final String PREFERENCES_DIRECTORY = ".imageEditor";
    /* the name of the preferences file that will be created on the user's filesystem */
    private static final String PREFERENCES_FILE = "/user-preferences.properties";
    /* self reference */
    private static PreferencesDataAccess dao;
    /* an object representation of the user preferences modified at runtime */
    private Properties newProperties;

    // Protected default constructor to avoid a new instance being requested from clients
    protected PreferencesDataAccess() {
        USER_HOME_DIRECTORY = System.getProperty("user.home");
    }

    // singleton instantiation of this data access object
    // guarantees only a single instance exists in the life of the application
    public static PreferencesDataAccess getInstance() {
        if (dao == null) {
            dao = new PreferencesDataAccess();
        }

        return dao;
    }

    /**
     * Builds the path associated with the preferences directory in the user's filesystem
     *
     * @return a path associated with the preferences directory in the user's filesystem
     */
    @Contract(pure = true)
    private @NotNull Path getUserPreferencesDirectoryPath() {
        return Path.of(USER_HOME_DIRECTORY, PREFERENCES_DIRECTORY);
    }


    private boolean userPreferencesDirectoryExists() {
        return Files.exists(this.getUserPreferencesDirectoryPath());
    }

    /**
     * Tries to create the user preferences directory
     */
    private void createUserPreferencesDirectory() throws IOException {
        try {
            Files.createDirectories(this.getUserPreferencesDirectoryPath());
        } catch (IOException e) {
            throw new IOException(String.format("Unable to create user preferences directory: %s\n", e.getMessage()));
        }
    }


    /**
     * Builds the path associated with the preferences file in the user's filesystem
     *
     * @return a path associated with the preferences file in the user's filesystem
     */
    @Contract(pure = true)
    private @NotNull Path getUserPreferencesFilePath() {
        return Path.of(USER_HOME_DIRECTORY, PREFERENCES_DIRECTORY, PREFERENCES_FILE);
    }

    // Whether the user preferences file exists
    private boolean userPreferencesFileExists() {
        return Files.exists(this.getUserPreferencesFilePath());
    }

    /**
     * Tries to create a user preferences file in the users filesystem
     *
     * @param defaultPreferences a properties file representing the default preferences
     */
    private void createUserPreferencesFile(Properties defaultPreferences) throws IOException {

        // Default properties cannot be null because the properties.load(inputStream) method
        // never returns null. In case of failure, it throws an exception instead of returning a null value.
        // doc: https://docs.oracle.com/javase/6/docs/api/java/util/Properties.html#load(java.io.InputStream)

        if (!userPreferencesDirectoryExists()) {
            // user preferences directory does not exist
            // create it
            this.createUserPreferencesDirectory();
        }
        //user preferences file exists, checked in getPreferences
        try {
            // create user preferences file (with default preferences)
            FileOutputStream outputStream = new FileOutputStream(String.valueOf(this.getUserPreferencesFilePath()));
            defaultPreferences.store(outputStream, null);

        } catch (IOException e) {
            throw new IOException(String.format("Unable to create user preferences file: %s\n", getUserPreferencesFilePath()));
        }

    }

    /**
     * Tries loading the default user preferences
     *
     * @return a properties object representing the default preferences
     */
    private @NotNull Properties loadDefaultPreferences() throws IOException {
        Properties defaultPreferences = new Properties();
        try {
            InputStream defaultPreferencesStream = getPreferencesResourceAsStream(DEFAULT_USER_PREFERENCES_PROPERTIES);
            defaultPreferences.load(defaultPreferencesStream);

        } catch (IOException e) {
            throw new IOException(String.format("Unable to load user preferences file: %s\n", getUserPreferencesFilePath()));
        }

        // return the properties object with the loaded preferences
        return defaultPreferences;
    }

    //per testing, non esisteva altro modo di testare l'exception in loaddefaultpreferences
    //dato che accede ai file nelle risorse e i metodi load e getResourceAs stream non sono mockabili
    protected InputStream getPreferencesResourceAsStream(String resource) {
        return this.getClass().getResourceAsStream(resource);
    }

    /**
     * Loads and returns the preferences stored in the preferences file
     *
     * @param path the path to the preferences file
     * @return a properties file representing the preferences, null if an exception was thrown
     */
    private @Nullable Properties loadPreferences(Path path) throws IOException {
        Properties preferences = new Properties();
        try {
            preferences.load(Files.newInputStream(path));
        } catch (IOException e) {
            throw new IOException(String.format("Unable to load user preferences file from the specified path: %s\n", path.toString()));
        }
        return preferences;
    }

    /**
     * Retrieves the properties object associated with the user's preferences. If no preferences file
     * exists yet, a new one is created.
     *
     * @return a properties object representing the user preferences
     */
    @Override
    public Properties getPreferences() throws IOException {

        /* an object representation of the user preferences in the filesystem */
        Properties userPreferences;
        if (userPreferencesFileExists()) {
            userPreferences = this.loadPreferences(this.getUserPreferencesFilePath());
            // the user preferences exist if we're here..
            newProperties = (Properties) userPreferences.clone();
            return userPreferences;
        }

        userPreferences = this.loadDefaultPreferences();
        newProperties = (Properties) userPreferences.clone();
        createUserPreferencesFile(userPreferences);

        // return the properties object with the loaded preferences
        return userPreferences;
    }

    /**
     * Stores the preference in the user preferences. This method works on a copy of the user preferences
     * stored in newProperties to only have an impact on the actual properties file in the filesystem and the copy
     * so that the current settings are not affected until an application restart
     *
     * @param preference the key-value pair representing the preference to be stored
     * @return true if the operation succeeded, false otherwise
     */
    @Override
    public boolean storePreference(Map.@NotNull Entry<String, String> preference) throws IOException {
        // Inizializza newProperties se non è già stato fatto
        if (newProperties == null) {
            newProperties = getPreferences();
        }

        // Imposta la preferenza specificata
        newProperties.setProperty(preference.getKey(), preference.getValue());

        // Usa try-with-resources per garantire la chiusura del FileOutputStream
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

