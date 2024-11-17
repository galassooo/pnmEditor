package ch.supsi.application.translations;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Defines the behavior that a generic translations model should expose to the controller.
 * This interface provides methods for managing supported languages, changing the application's
 * language, and retrieving translations or resource bundles for UI components.
 */
public interface TranslationsBusinessInterface {

    /**
     * Retrieves a list of all languages supported by the application.
     *
     * @return a {@link List} of strings representing the supported languages
     */
    List<String> getSupportedLanguages();

    /**
     * Changes the current language of the application.
     *
     * @param languageTag the language tag (e.g., "en", "it") to set as the current language
     * @return {@code true} if the language was successfully changed, {@code false} otherwise
     */
    @SuppressWarnings("all")
    //unused return type
    boolean changeLanguage(String languageTag);

    /**
     * Retrieves the translation associated with the specified key.
     *
     * @param key the key associated with the desired translation
     * @return an {@link Optional} containing the translation if found, or empty if the key does not exist
     */
    Optional<String> translate(String key);

    /**
     * Retrieves the UI resource bundle for the specified locale.
     *
     * @param locale the selected locale for which the resource bundle is required
     * @return an {@link Optional} containing the {@link ResourceBundle} if found, or empty if not available
     */
    Optional<ResourceBundle> getUIResourceBundle(Locale locale);
}
