package ch.supsi.application.translations;


import ch.supsi.application.preferences.PreferencesBusinessInterface;
import ch.supsi.business.preferences.PreferencesBusiness;
import ch.supsi.business.translations.TranslationBusiness;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class TranslationsApplication {

    /* self reference */
    private static TranslationsApplication myself;

    /* business references */
    private final TranslationsBusinessInterface translationsModel;

    private TranslationsApplication() throws IOException {
        PreferencesBusinessInterface preferencesModel = PreferencesBusiness.getInstance();
        this.translationsModel = TranslationBusiness.getInstance();

        String currentLanguage = preferencesModel.getCurrentLanguage();
        this.translationsModel.changeLanguage(currentLanguage);
    }

    public static TranslationsApplication getInstance() throws IOException {
        if (myself == null) {
            myself = new TranslationsApplication();
        }

        return myself;
    }

    /**
     * Delegates the retrieval of all supported languages to the model.
     *
     * @return a list representing all supported languages in this application
     */
    public List<String> getSupportedLanguages() {
        return this.translationsModel.getSupportedLanguages();
    }


    /**
     * Delegates the retrieval of the translation associated with the given key to the model
     *
     * @param key the key associated with a key-value pair representing the translation
     * @return the translation associated with the given key (the value in the key-value pair)
     */
    public String translate(String key) {
        return this.translationsModel.translate(key);
    }

    /**
     * Return the UI translation bundle with the given locale
     *
     * @param locale selected locale
     * @return UI resource bundle
     */
    public ResourceBundle getTranslationBundle(Locale locale) {
        return this.translationsModel.getUIResourceBundle(locale);
    }
}

