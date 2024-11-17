package org.supsi.model.translations;

import ch.supsi.application.preferences.PreferencesApplication;
import ch.supsi.application.translations.TranslationsApplication;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class TranslationModel implements ITranslationsModel {

    /* backend controllers */
    private static TranslationsApplication translationsController;
    private static final PreferencesApplication preferencesController;
    /* self reference */
    private static TranslationModel instance;

    // Initialize controllers
    static {

        translationsController = TranslationsApplication.getInstance();

        preferencesController = PreferencesApplication.getInstance();
    }

    /* data */
    private ResourceBundle uiBundle;
    private Locale locale;

    private TranslationModel() {
    }

    /**
     * @return an instance of this class
     */
    public static TranslationModel getInstance() {
        if (instance == null) {
            instance = new TranslationModel();
        }
        return instance;
    }

    /**
     * Attempts to load the resource bundle containing the labels useful for the FXML components of the frontend
     *
     * @return The bundle, null if it wasn't found
     */
    public ResourceBundle getUiBundle() throws IOException {
        if (uiBundle == null) { //load only once
            uiBundle = translationsController.getTranslationBundle(getLocale()).orElseThrow(
                    () -> new IOException("Translation bundle not found")
            );
        }
        return uiBundle;
    }

    /**
     * Attempts to load the current locale based on user's preferences
     *
     * @return The Locale, null if it wasn't found
     */
    @Override
    public Locale getLocale() {
        if (locale == null) { //load only once
            locale = Locale.forLanguageTag(preferencesController.getPreference("language-tag").orElse("N/A").toString());
        }
        return locale;
    }

    /**
     * Returns a translated string in the selected language given the associated key
     *
     * @param s string key
     * @return translated string
     */
    @Override
    public String translate(String s) {
        return translationsController.translate(s).orElse("Translation not found");
    }


    /**
     * Retrieves the list of supported languages available for translation.
     *
     * @return A list of language tags representing supported languages.
     */
    @Override
    public List<String> getSupportedLanguages() {
        return translationsController.getSupportedLanguages();
    }

    /**
     * Retrieves the current language selected by the user based on preferences.
     *
     * @return The Locale object representing the current language.
     */
    @Override
    public Locale getCurrentLanguage() {
        return Locale.forLanguageTag(String.valueOf(preferencesController.getPreference("language-tag")));
    }
}
