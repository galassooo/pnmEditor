package ch.supsi.application.translations;

import ch.supsi.application.preferences.PreferencesBusinessInterface;
import ch.supsi.business.preferences.PreferencesBusiness;
import ch.supsi.business.translations.TranslationBusiness;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Provides an application-level interface for managing translations and language settings.
 * Implements the Singleton pattern to ensure a single instance is used throughout the application.
 */
public class TranslationsApplication {

    private static TranslationsApplication myself;
    private final TranslationsBusinessInterface translationsModel;

    /**
     * Private constructor to enforce the Singleton pattern.
     * Initializes the translation model and sets the current language based on user preferences.
     */
    private TranslationsApplication() {
        PreferencesBusinessInterface preferencesModel = PreferencesBusiness.getInstance();
        this.translationsModel = TranslationBusiness.getInstance();

        // Set the current language based on user preferences
        String currentLanguage = preferencesModel.getCurrentLanguage().orElse("N/A");
        this.translationsModel.changeLanguage(currentLanguage);
    }

    /**
     * Retrieves the Singleton instance of the {@link TranslationsApplication}.
     *
     * @return the Singleton instance of this class
     */
    public static TranslationsApplication getInstance() {
        if (myself == null) {
            myself = new TranslationsApplication();
        }
        return myself;
    }

    /**
     * Retrieves the translation associated with the specified key.
     * Delegates the operation to the translations business model.
     *
     * @param key the key associated with the desired translation
     * @return an {@link Optional} containing the translation if found, or empty if not found
     */
    public Optional<String> translate(String key) {
        return this.translationsModel.translate(key);
    }

    /**
     * Retrieves a list of all supported languages in the application.
     * Delegates the operation to the translations business model.
     *
     * @return a {@link List} of strings representing the supported languages
     */
    public List<String> getSupportedLanguages() {
        return this.translationsModel.getSupportedLanguages();
    }

    /**
     * Retrieves the UI translation bundle for the specified locale.
     * Delegates the operation to the translations business model.
     *
     * @param locale the selected locale for which the resource bundle is required
     * @return an {@link Optional} containing the {@link ResourceBundle} if found, or empty if not found
     */
    public Optional<ResourceBundle> getTranslationBundle(Locale locale) {
        return this.translationsModel.getUIResourceBundle(locale);
    }
}
