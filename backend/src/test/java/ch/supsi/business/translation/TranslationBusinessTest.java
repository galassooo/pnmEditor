package ch.supsi.business.translation;

import ch.supsi.business.translations.TranslationBusiness;
import ch.supsi.business.translations.TranslationsDataAccessInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TranslationBusinessTest {

    private TranslationBusiness translationBusiness;
    private TranslationsDataAccessInterface mockDataAccess;

    @BeforeEach
    void setUp() throws Exception {
        //Reset singleton using reflection to ensure isolayion
        Field instanceField = TranslationBusiness.class.getDeclaredField("myself");
        instanceField.setAccessible(true);
        instanceField.set(null, null);

        // mock dao
        mockDataAccess = mock(TranslationsDataAccessInterface.class);
        Properties enProperties = new Properties();
        enProperties.setProperty("hello", "Hello");
        enProperties.setProperty("bye", "Goodbye");

        Properties itProperties = new Properties();
        itProperties.setProperty("hello", "Ciao");
        itProperties.setProperty("bye", "Addio");

        when(mockDataAccess.getSupportedLanguageTags()).thenReturn(List.of("en-US", "it-CH"));
        when(mockDataAccess.getTranslations(Locale.forLanguageTag("en-US"))).thenReturn(enProperties);
        when(mockDataAccess.getTranslations(Locale.forLanguageTag("it-CH"))).thenReturn(itProperties);
        when(mockDataAccess.getUIResourceBundle(Locale.forLanguageTag("en-US"))).thenReturn(Optional.empty());
        when(mockDataAccess.getUIResourceBundle(Locale.forLanguageTag("it-CH"))).thenReturn(Optional.empty());

        //create a new instance
        Constructor<TranslationBusiness> constructor = TranslationBusiness.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        translationBusiness = constructor.newInstance();

        //configure dao field
        Field daoField = TranslationBusiness.class.getDeclaredField("translationsDao");
        daoField.setAccessible(true);
        daoField.set(translationBusiness, mockDataAccess);
    }

    @Test
    void testGetSupportedLanguages() {
        List<String> supportedLanguages = translationBusiness.getSupportedLanguages();
        assertEquals(List.of("en-US", "it-CH"), supportedLanguages, "Supported languages should match the mock values");
    }

    @Test
    void testChangeLanguageSuccess() {
        assertTrue(translationBusiness.changeLanguage("en-US"), "Changing to a supported language should return true");
        assertTrue(translationBusiness.changeLanguage("it-CH"), "Changing to another supported language should return true");
    }

    @Test
    void testChangeLanguageFail() {
        assertFalse(translationBusiness.changeLanguage("fr-FR"), "Changing to an unsupported language should return false");
    }

    @Test
    void testTranslateKeyExists() {
        translationBusiness.changeLanguage("en-US");
        assertEquals(Optional.of("Hello"), translationBusiness.translate("hello"), "The translation for 'hello' should be 'Hello'");

        translationBusiness.changeLanguage("it-CH");
        assertEquals(Optional.of("Ciao"), translationBusiness.translate("hello"), "The translation for 'hello' should be 'Ciao'");
    }

    @Test
    void testTranslateKeyNotExists() {
        translationBusiness.changeLanguage("en-US");
        assertEquals(Optional.empty(), translationBusiness.translate("unknown_key"), "Translation for an unknown key should be empty");
    }

    @Test
    void testGetUIResourceBundle() {
        Optional<ResourceBundle> enResourceBundle = translationBusiness.getUIResourceBundle(Locale.forLanguageTag("en-US"));
        assertTrue(enResourceBundle.isEmpty(), "Mocked ResourceBundle for 'en-US' should be empty");

        Optional<ResourceBundle> itResourceBundle = translationBusiness.getUIResourceBundle(Locale.forLanguageTag("it-CH"));
        assertTrue(itResourceBundle.isEmpty(), "Mocked ResourceBundle for 'it-CH' should be empty");
    }

    @Test
    void testSingleton(){
        TranslationBusiness business = TranslationBusiness.getInstance();
        TranslationBusiness other = TranslationBusiness.getInstance();

        assertEquals(business, other, "The singleton should be the same");
    }
}

