package org.supsi.model.translations;

import ch.supsi.application.preferences.PreferencesApplication;
import ch.supsi.application.translations.TranslationsApplication;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Represents the model for managing translations and language settings in the application.
 * Utilizes the controllers to retrieve and cache translations and user preferences.
 */
public class TranslationModel implements ITranslationsModel {

    private static TranslationModel myself;
    private static final TranslationsApplication translationsController;
    private static final PreferencesApplication preferencesController;

    private ResourceBundle uiBundle;
    private Locale locale;

    static {
        translationsController = TranslationsApplication.getInstance();
        preferencesController = PreferencesApplication.getInstance();
    }

    private TranslationModel() {}

    /**
     * Retrieves the singleton instance of this class.
     *
     * @return the singleton instance of {@code TranslationModel}
     */
    public static TranslationModel getInstance() {
        if (myself == null) {
            myself = new TranslationModel();
        }
        return myself;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResourceBundle getUiBundle() throws IOException {
        if (uiBundle == null) { //load only once
            uiBundle = translationsController.getTranslationBundle(getLocale()).orElseThrow(
                    () -> new IOException("Translation bundle not found")
            );
        }
        return uiBundle;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Locale getLocale() {
        if (locale == null) { //load only once
            locale = Locale.forLanguageTag(preferencesController.getPreference("language-tag").orElse("N/A").toString());
        }
        return locale;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String translate(String s) {
        return translationsController.translate(s).orElse("Translation not found");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getSupportedLanguages() {
        return translationsController.getSupportedLanguages();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Locale getCurrentLanguage() {
        return Locale.forLanguageTag(String.valueOf(preferencesController.getPreference("language-tag")));
    }
}
