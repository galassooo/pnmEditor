package org.supsi.model.translations;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Defines the behavior that a translations model should expose to the view and controller.
 * Provides methods for retrieving and interacting with supported translations and languages.
 */
public interface ITranslationsModel {

    /**
     * Retrieves the current language of the application.
     *
     * @return the current language, typically as an object representing a language tag or identifier
     */
    Object getCurrentLanguage();

    /**
     * Translates a given key into the current language.
     *
     * @param s the key to translate
     * @return the translated value, or the key itself if no translation is found
     */
    String translate(String s);

    /**
     * Retrieves the UI resource bundle containing translations for the user interface.
     *
     * @return a {@link ResourceBundle} with UI translations
     * @throws IOException if there is an issue loading the resource bundle
     */
    ResourceBundle getUiBundle() throws IOException;

    /**
     * Retrieves a list of supported language tags for the application.
     *
     * @return a {@link List} of supported language tags
     */
    List<String> getSupportedLanguages();

    /**
     * Retrieves the current locale of the application.
     *
     * @return the current {@link Locale}
     */
    Locale getLocale();
}