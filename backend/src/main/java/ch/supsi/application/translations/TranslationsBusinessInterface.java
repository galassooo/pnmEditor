package ch.supsi.application.translations;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Defines the behaviour that a generic translations model should expose to the controller
 */
public interface TranslationsBusinessInterface {

    List<String> getSupportedLanguages();

    boolean changeLanguage(String languageTag);

    Optional<String> translate(String key);
    Optional<ResourceBundle> getUIResourceBundle(Locale locale);

}
