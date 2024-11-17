package ch.supsi.dataaccess.translations;

import ch.supsi.business.translations.TranslationsDataAccessInterface;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Provides access to translations and supported languages for both backend and frontend modules.
 * It uses properties files to load translations and handles locale fallback mechanisms.
 */
public class TranslationsDataAccess implements TranslationsDataAccessInterface {

    /* Singleton instance */
    private static TranslationsDataAccess myself;

    /* Path to the file where supported languages are stored */
    private static final String SUPPORTED_LANGUAGES_PROPERTIES = "/supported-languages.properties";

    /* Path where translation labels are stored */
    private static final String LABELS_PATH = "i18n/labels/";

    /* Format of the files containing the translation labels */
    private static final String LABELS_FORMAT = ".properties";

    /* Path for frontend UI labels */
    private static final String FRONTEND_PATH = "/i18n/ui_labels";

    protected TranslationsDataAccess() {
    }

    /**
     * Singleton instantiation method for TranslationsDataAccess.
     *
     * @return the singleton instance of TranslationsDataAccess
     */
    public static TranslationsDataAccess getInstance() {
        if (myself == null) {
            myself = new TranslationsDataAccess();
        }

        return myself;
    }

    /**
     * Retrieves the resource bundle containing UI labels for the specified locale.
     * If the locale is invalid, a fallback locale is used.
     *
     * @param locale the locale to load UI labels for
     * @return an {@link Optional} containing the ResourceBundle for the specified locale, or empty if not found
     */
    @Override
    public Optional<ResourceBundle> getUIResourceBundle(Locale locale) {
        List<ResourceBundle> bundles = loadFrontendResources(locale);
        if (bundles.isEmpty()) {
            Locale fallbackLocale = Locale.forLanguageTag(this.getSupportedLanguageTags().get(0));
            bundles = handleMissingResource(locale, fallbackLocale);
        }
        return bundles.isEmpty() ? Optional.empty() : Optional.of(bundles.get(0));
    }

    /**
     * Retrieves a list of supported language tags loaded from a properties file.
     *
     * @return a {@link List} of strings representing the supported language tags
     */
    @Override
    public List<String> getSupportedLanguageTags() {
        List<String> supportedLanguageTags = new ArrayList<>();
        Properties props = this.loadSupportedLanguageTags();
        for (Object key : props.keySet()) {
            supportedLanguageTags.add(props.getProperty((String) key));
        }
        return supportedLanguageTags;
    }

    /**
     * Loads translations for the given locale. If the locale is invalid, a fallback
     * mechanism loads the default locale translations.
     *
     * @param locale the locale to load translations for
     * @return a {@link Properties} object containing the translations for the specified locale
     */
    @Override
    public Properties getTranslations(Locale locale) {
        Properties translations = new Properties();

        List<ResourceBundle> bundles = getResourceBundlesForLocale(locale);
        if (bundles.isEmpty()) {
            Locale fallbackLocale = Locale.forLanguageTag(this.getSupportedLanguageTags().get(0));
            bundles = handleMissingResource(locale, fallbackLocale);
        }
        bundles.forEach((b) -> {
            for (String key : b.keySet()) {
                translations.setProperty(key, b.getString(key));
            }
        });

        return translations;
    }

    /**
     * Loads and returns the supported language tags.
     * The supported language tags are loaded from a default properties file.
     *
     * @return a {@link Properties} object representing the supported language tags
     */
    private @NotNull Properties loadSupportedLanguageTags() {
        Properties supportedLanguageTags = new Properties();
        try (InputStream supportedLanguageTagsStream = this.getClass().getResourceAsStream(SUPPORTED_LANGUAGES_PROPERTIES)) {
            if (supportedLanguageTagsStream != null) {
                supportedLanguageTags.load(supportedLanguageTagsStream);
            }
        } catch (IOException e) {
            System.err.printf("Error while loading file %s%n", SUPPORTED_LANGUAGES_PROPERTIES);
        }

        return supportedLanguageTags;
    }


    /**
     * Retrieves all resource bundles for the specified locale.
     *
     * @param locale the locale to load resource bundles for
     * @return a {@link List} of {@link ResourceBundle} objects for the specified locale
     */
    private @NotNull List<ResourceBundle> getResourceBundlesForLocale(Locale locale) {
        List<ResourceBundle> resourceBundles = new ArrayList<>();

        resourceBundles.addAll(loadBackendResources(locale));
        resourceBundles.addAll(loadFrontendResources(locale));

        return resourceBundles;
    }

    /**
     * Loads all resources associated with the specified locale from the backend module.
     *
     * @param locale the locale to load backend resources for
     * @return a {@link List} of {@link ResourceBundle} objects from the backend module
     */
    private @NotNull List<ResourceBundle> loadBackendResources(Locale locale) {
        List<ResourceBundle> resourceBundles = new ArrayList<>();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        try {
            Resource[] resources = resolver.getResources(String.format("classpath:%s*%s", TranslationsDataAccess.LABELS_PATH, LABELS_FORMAT));
            for (Resource resource : resources) {
                String filename = resource.getFilename();
                if (filename != null && filename.contains(String.format("_%s%s", locale, LABELS_FORMAT))) {
                    try (InputStreamReader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
                        ResourceBundle resourceBundle = new PropertyResourceBundle(reader);
                        resourceBundles.add(resourceBundle);
                    }
                }
            }
        } catch (IOException e) {
            System.err.printf("Error while loading file %s for locale %s%n", TranslationsDataAccess.LABELS_PATH, locale);
        }

        return resourceBundles;
    }

    /**
     * Loads all resources associated with the specified locale from the frontend module.
     *
     * @param locale the locale to load frontend resources for
     * @return a {@link List} of {@link ResourceBundle} objects from the frontend module
     */
    private @NotNull List<ResourceBundle> loadFrontendResources(Locale locale) {
        List<ResourceBundle> resourceBundles = new ArrayList<>();
        Optional<Module> frontendModule = ModuleLayer.boot().findModule("frontend");

        if (frontendModule.isPresent()) {
            Optional<String> frontendPath = loadFrontendPath();

            if (frontendPath.isPresent()) {
                String localeCode = locale.toLanguageTag().replace('-', '_');
                String resourceName = String.format("%s_%s.properties", frontendPath, localeCode);

                try (InputStream inputStream = frontendModule.get().getResourceAsStream(resourceName)) {
                    if (inputStream != null) {
                        try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                            ResourceBundle resourceBundle = new PropertyResourceBundle(reader);
                            resourceBundles.add(resourceBundle);
                        }
                    }
                } catch (IOException e) {
                    System.err.printf("Error loading resource %s from the frontend module%n", resourceName);
                }
            }
        } else { // JAR
            String localeCode = locale.toLanguageTag().replace('-', '_');
            String resourceName = String.format("%s_%s.properties", FRONTEND_PATH.substring(1), localeCode);

            try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(resourceName)) {
                if (inputStream != null) {
                    try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                        ResourceBundle resourceBundle = new PropertyResourceBundle(reader);
                        resourceBundles.add(resourceBundle);
                    }
                } else {
                    System.err.printf("Resource not found: %s in the JAR%n", resourceName);
                }
            } catch (IOException e) {
                System.err.printf("Error loading resource %s from JAR%n", resourceName);
            }
        }

        return resourceBundles;
    }

    /**
     * Loads the path for frontend labels from the `application.properties` file.
     *
     * @return an {@link Optional} containing the frontend labels path
     */
    private Optional<String> loadFrontendPath() {
        try (InputStream is = TranslationsDataAccess.class.getClassLoader().getResourceAsStream("application.properties")) {
            Properties properties = new Properties();
            if (is != null) {
                properties.load(new InputStreamReader(is, StandardCharsets.UTF_8));
                return Optional.of(properties.getProperty("frontend.labels.path"));
            } else {
                System.err.println("Application.properties file not found.");
            }
        } catch (IOException e) {
            System.err.println("Error while loading application.properties");
        }
        return Optional.empty();
    }

    /**
     * Handles loading resources when the specified locale is invalid.
     *
     * @param invalidLocale  the invalid locale
     * @param fallbackLocale the fallback locale to use
     * @return a {@link List} of ResourceBundle objects for the fallback locale
     */
    private @NotNull List<ResourceBundle> handleMissingResource(@NotNull Locale invalidLocale, @NotNull Locale fallbackLocale) {
        System.err.printf("Invalid locale: %s. Loading new locale: %s%n", invalidLocale, fallbackLocale);
        return getResourceBundlesForLocale(fallbackLocale);
    }
}
