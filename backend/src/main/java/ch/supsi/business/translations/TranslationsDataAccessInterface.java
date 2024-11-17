package ch.supsi.business.translations;

import java.util.*;

/**
 * Defines the behaviour that a generic translations data access should expose to the model
 */
public interface TranslationsDataAccessInterface {
    Optional<ResourceBundle> getUIResourceBundle(Locale locale);

    List<String> getSupportedLanguageTags();

    Properties getTranslations(Locale locale);
}
