package ch.supsi.business.translations;

import ch.supsi.application.translations.TranslationsBusinessInterface;
import ch.supsi.dataaccess.translations.TranslationsDataAccess;

import java.util.*;

/**
 * Provides the business logic for managing translations in the application.
 * This class interacts with the data access layer to retrieve and manage translations,
 * supported languages, and UI resource bundles.
 * Implements the Singleton pattern to ensure a single instance is used across the application.
 */
public class TranslationBusiness implements TranslationsBusinessInterface {

    private static TranslationBusiness myself;

    private final TranslationsDataAccessInterface translationsDao;
    private final List<String> supportedLanguageTags;
    private Properties translations;

    /** Protected constructor to enforce Singleton pattern */
    protected TranslationBusiness() {
        this.translationsDao = TranslationsDataAccess.getInstance();
        this.supportedLanguageTags = translationsDao.getSupportedLanguageTags();
    }

    /**
     * Retrieves the Singleton instance of {@link TranslationBusiness}.
     *
     * @return the Singleton instance
     */
    public static TranslationBusiness getInstance() {
        if (myself == null) {
            myself = new TranslationBusiness();
        }
        return myself;
    }

    /**
     * Retrieves all supported languages for the application.
     *
     * @return a {@link List} containing all supported language tags
     */
    @Override
    public List<String> getSupportedLanguages() {
        return List.copyOf(this.supportedLanguageTags); // Defensive copy
    }

    /**
     * Changes the current language of the application by loading the corresponding translations.
     *
     * @param languageTag the language tag (e.g., "en", "it") for the desired language
     * @return {@code true} if the language was successfully changed, {@code false} otherwise
     */
    @Override
    public boolean changeLanguage(String languageTag) {
        this.translations = translationsDao.getTranslations(Locale.forLanguageTag(languageTag));
        return this.translations != null;
    }


    /**
     * Retrieves the translation associated with the specified key.
     *
     * @param key the key for the desired translation
     * @return an {@link Optional} containing the translation if found, or empty if the key does not exist
     */
    @Override
    public Optional<String> translate(String key) {
        return Optional.ofNullable(this.translations.getProperty(key));
    }

    /**
     * Retrieves the UI resource bundle for the specified locale.
     *
     * @param locale the {@link Locale} for which the resource bundle is required
     * @return an {@link Optional} containing the {@link ResourceBundle} if found, or empty if not available
     */
    @Override
    public Optional<ResourceBundle> getUIResourceBundle(Locale locale) {
        return this.translationsDao.getUIResourceBundle(locale);
    }
}
