package ch.supsi.business.translations;

import java.util.*;

/**
 * Defines the behavior that a generic translations data access object should expose to the business logic.
 * This interface provides methods to retrieve translations, supported languages, and resource bundles for UI components.
 */
public interface TranslationsDataAccessInterface {

    /**
     * Retrieves the resource bundle containing UI labels for the specified locale.
     * If the resource bundle for the specified locale is not available, a fallback mechanism
     * may be implemented to provide default translations.
     *
     * @param locale the locale for which the UI resource bundle is required
     * @return an {@link Optional} containing the resource bundle, or empty if no bundle is found
     */
    Optional<ResourceBundle> getUIResourceBundle(Locale locale);

    /**
     * Retrieves the list of supported language tags.
     * The language tags are typically stored in a properties file and represent the locales
     * supported by the application.
     *
     * @return a {@link List} of strings representing the supported language tags
     */
    List<String> getSupportedLanguageTags();

    /**
     * Retrieves the translations for the specified locale as a {@link Properties} object.
     * This method may implement a fallback mechanism to provide translations for a default locale
     * if the requested locale is not available.
     *
     * @param locale the locale for which translations are required
     * @return a {@link Properties} object containing the translations for the specified locale
     */
    Properties getTranslations(Locale locale);
}
