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

public class TranslationsDataAccess implements TranslationsDataAccessInterface {
    /* where the supported languages are stored */
    private static final String SUPPORTED_LANGUAGES_PROPERTIES = "/supported-languages.properties";
    /* where the labels are stored */
    private static final String LABELS_PATH = "i18n/labels/";
    /* the format of the files containing the labels */
    private static final String LABELS_FORMAT = ".properties";

    private static String FRONTEND_PATH = "/i18n/ui_labels";

    /* self reference */
    public static TranslationsDataAccess myself;

    protected TranslationsDataAccess() {
    }

    // Singleton instantiation
    public static TranslationsDataAccess getInstance() {
        if (myself == null) {
            myself = new TranslationsDataAccess();
        }

        return myself;
    }
    /**
     * Loads and returns the supported language tags. The supported language tags are loaded from a
     * default properties file
     *
     * @return a properties file representing the supported language tags properties file
     */
    private @NotNull Properties loadSupportedLanguageTags() {
        Properties supportedLanguageTags = new Properties();
        try (InputStream supportedLanguageTagsStream = this.getClass().getResourceAsStream(SUPPORTED_LANGUAGES_PROPERTIES)) {
            if (supportedLanguageTagsStream != null) {
                supportedLanguageTags.load(supportedLanguageTagsStream);
            }
        } catch (IOException e) {
            System.err.printf("Error while loading file %s%n", SUPPORTED_LANGUAGES_PROPERTIES);
            e.printStackTrace();
        }

        return supportedLanguageTags;
    }

    /**
     * Retrieves a list of strings representing the supported language tags, retrieved from a properties file
     *
     * @return a list of strings representing the supported language tags
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
     * Tries loading the translations for the given locale. If the locale is not valid, this method has a fallback
     * mechanism that loads a default, valid language if the provided locale is not valid
     *
     * @param locale the locale that we want to load the translations for
     * @return a properties object representing all the translations for the given locale, or for the fallback locale if the
     * provided locale was not valid
     */
    @Override
    public Properties getTranslations(Locale locale) {
        Properties translations = new Properties();

        List<ResourceBundle> bundles = getResourceBundlesForLocale(locale, LABELS_PATH);
        // It means it failed to load translations for the given locale, fallback to a default one
        if (bundles.isEmpty()) {
            Locale fallbackLocale = Locale.forLanguageTag(this.getSupportedLanguageTags().get(0));
            // This assumes that the pathToResources is valid, and the only thing that's not valid is the locale
            bundles = handleMissingResource(locale, fallbackLocale, LABELS_PATH);
        }
        bundles.forEach((b) -> {
            for (String key : b.keySet()) {
                translations.setProperty(key, b.getString(key));
            }
        });

        return translations;
    }

    private @NotNull List<ResourceBundle> getResourceBundlesForLocale(Locale locale, String pathToResources) {
        List<ResourceBundle> resourceBundles = new ArrayList<>();


        resourceBundles.addAll(loadBackendResources(locale, pathToResources));

        // Carica risorse dal modulo frontend
        resourceBundles.addAll(loadFrontendResources(locale));

        return resourceBundles;
    }

    /**
     * Carica tutte le risorse associate al locale specificato dal modulo backend.
     */
    private @NotNull List<ResourceBundle> loadBackendResources(Locale locale, String pathToResources) {
        List<ResourceBundle> resourceBundles = new ArrayList<>();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        try {
            Resource[] resources = resolver.getResources(String.format("classpath:%s*%s", pathToResources, LABELS_FORMAT));
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
            System.err.printf("Error while loading file %s for locale %s%n", pathToResources, locale);
            e.printStackTrace();
        }

        return resourceBundles;
    }

    /**
     * Carica tutte le risorse associate al locale specificato dal modulo frontend.
     */
    private @NotNull List<ResourceBundle> loadFrontendResources(Locale locale) {
        List<ResourceBundle> resourceBundles = new ArrayList<>();
        Optional<Module> frontendModule = ModuleLayer.boot().findModule("frontend");

        if (frontendModule.isPresent()) {
            String frontendPath = loadFrontendPath();

            if (frontendPath != null) {
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
                    System.err.printf("Errore nel caricamento della risorsa %s dal modulo frontend%n", resourceName);
                    e.printStackTrace();
                }
            }
        } else { //jar
            String localeCode = locale.toLanguageTag().replace('-', '_');
            String resourceName = String.format("%s_%s.properties", FRONTEND_PATH.substring(1), localeCode);

            try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(resourceName)) {
                if (inputStream != null) {
                    try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                        ResourceBundle resourceBundle = new PropertyResourceBundle(reader);
                        resourceBundles.add(resourceBundle);
                    }
                } else {
                    System.err.printf("Risorsa non trovata: %s nel JAR%n", resourceName);
                }
            } catch (IOException e) {
                System.err.printf("Errore nel caricamento della risorsa %s dal JAR%n", resourceName);
                e.printStackTrace();
            }
        }

        return resourceBundles;
    }

    /**
     * Carica il percorso `frontend.labels.path` dal file `application.properties`.
     */
    private String loadFrontendPath() {
        try (InputStream is = TranslationsDataAccess.class.getClassLoader().getResourceAsStream("application.properties")) {
            Properties properties = new Properties();
            if (is != null) {
                properties.load(new InputStreamReader(is, StandardCharsets.UTF_8));
                return properties.getProperty("frontend.labels.path");
            } else {
                System.err.println("File application.properties non trovato.");
            }
        } catch (IOException e) {
            System.err.println("Errore durante il caricamento di application.properties.");
            e.printStackTrace();
        }
        return null;
    }



    /**
     * This method handles loading resources in case of an invalid locale
     *
     * @param invalidLocale   the invalid locale
     * @param fallbackLocale  a new locale
     * @param pathToResources the path to the resources to be loaded
     * @return a list of resource bundles associated with the given fallbackLocale
     */
    private @NotNull List<ResourceBundle> handleMissingResource(@NotNull Locale invalidLocale, @NotNull Locale fallbackLocale, String pathToResources) {
        System.err.printf("Invalid locale: %s. Loading new locale: %s\n", invalidLocale, fallbackLocale);
        return getResourceBundlesForLocale(fallbackLocale, pathToResources);
    }

    /**
     * Retrieves the resource bundle containing the UI labels
     *
     * @param locale the locale that we want to load the translations for
     * @return a resource bundle containing the UI labels
     */
    @Override
    public ResourceBundle getUIResourceBundle(Locale locale) {
        List<ResourceBundle> bundles = loadFrontendResources(locale);
        if (bundles.isEmpty()) {
            Locale fallbackLocale = Locale.forLanguageTag(this.getSupportedLanguageTags().get(0));
            bundles = handleMissingResource(locale, fallbackLocale, LABELS_PATH);
        }
        return bundles.get(0);
    }
}
