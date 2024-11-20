package ch.supsi.application.translation;

import ch.supsi.application.translations.TranslationsApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;

public class TranslationsApplicationTest {

    private TranslationsApplication translationsApplication;

    @BeforeEach
    void setUp() {
        translationsApplication = TranslationsApplication.getInstance();
    }

    @Test
    void testGetSupportedLanguages() {
        List<String> supportedLanguages = translationsApplication.getSupportedLanguages();
        assertNotNull(supportedLanguages, "Supported languages should not be null");
        assertFalse(supportedLanguages.isEmpty(), "There should be at least one supported language");
    }

    @Test
    void testTranslateExistingKey() {
        Optional<String> translation = translationsApplication.translate("Negative");
        assertTrue(translation.isPresent(), "Translation for 'Negative' should be present");
        assertFalse(translation.get().isEmpty(), "Translation for 'Negative' should not be empty");
    }

    @Test
    void testTranslateNonExistingKey() {
        Optional<String> translation = translationsApplication.translate("non_existing_key");
        assertFalse(translation.isPresent(), "Translation for a non-existing key should not be present");
    }

    @Test
    void testGetTranslationBundle() {
        Optional<ResourceBundle> bundle = translationsApplication.getTranslationBundle(Locale.ENGLISH);
        assertNotNull(bundle, "Translation bundle should not be null");
    }
}
