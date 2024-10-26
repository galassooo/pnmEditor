package ch.supsi.application.translations;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Defines the behaviour that a generic translations model should expose to the controller
 */
public interface TranslationsBusinessInterface {

    boolean isSupportedLanguageTag(String languageTag);

    List<String> getSupportedLanguages();

    boolean changeLanguage(String languageTag);

    String translate(String key);

    ResourceBundle getUIResourceBundle(Locale locale);

}
