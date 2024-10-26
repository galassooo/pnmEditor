package ch.supsi.business.translations;

import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Defines the behaviour that a generic translations data access should expose to the model
 */
public interface TranslationsDataAccessInterface {
    ResourceBundle getUIResourceBundle(Locale locale);

    List<String> getSupportedLanguageTags();

    Properties getTranslations(Locale locale);
}
