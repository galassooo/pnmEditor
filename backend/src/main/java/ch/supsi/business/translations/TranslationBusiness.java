package ch.supsi.business.translations;

import ch.supsi.application.translations.TranslationsBusinessInterface;
import ch.supsi.dataaccess.translations.TranslationsDataAccess;

import java.io.IOException;
import java.util.*;

public class TranslationBusiness implements TranslationsBusinessInterface {

    /* self reference */
    private static TranslationBusiness myself;

    /* data access reference */
    private final TranslationsDataAccessInterface translationsDao;

    /* fields */
    private final List<String> supportedLanguageTags;

    private Properties translations;

    /* constructor */
    protected TranslationBusiness() {
        this.translationsDao = TranslationsDataAccess.getInstance();
        this.supportedLanguageTags = translationsDao.getSupportedLanguageTags();
    }

    public static TranslationBusiness getInstance() {
        if (myself == null) {
            myself = new TranslationBusiness();
        }
        return myself;
    }

    /**
     * Retrieves all supported languages of this application
     *
     * @return a {@link List} containing all supported languages
     */
    @Override
    public List<String> getSupportedLanguages() {
        return List.copyOf(this.supportedLanguageTags); // Defensive copy
    }

    /**
     * Changes the language of this application
     *
     * @param languageTag the language tag associated with the language to switch to
     * @return whether the language was successfully changed
     */
    @Override
    public boolean changeLanguage(String languageTag) {
        this.translations = translationsDao.getTranslations(Locale.forLanguageTag(languageTag));
        return this.translations != null;
    }


    /**
     * Retrieves the translation associated with the given key
     *
     * @param key the key associated with a key-value pair representing the translation
     * @return the translation associated with the given key (the value in the key-value pair)
     */
    @Override
    public Optional<String> translate(String key) {
        return Optional.of(this.translations.getProperty(key));
    }

    /**
     * Return the UI translation bundle with the given locale
     *
     * @param locale selected locale
     * @return UI {@link ResourceBundle}
     */
    @Override
    public Optional<ResourceBundle> getUIResourceBundle(Locale locale) {
        return this.translationsDao.getUIResourceBundle(locale);
    }
}
