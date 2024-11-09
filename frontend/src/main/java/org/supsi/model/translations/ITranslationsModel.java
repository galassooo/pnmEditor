package org.supsi.model.translations;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Defines the behaviour that a generic translations model should expose to the view and controller
 */
public interface ITranslationsModel {
    Object getCurrentLanguage();

    String translate(String s);
    ResourceBundle getUiBundle() throws IOException;
    List<String> getSupportedLanguages();
    Locale getLocale();
}