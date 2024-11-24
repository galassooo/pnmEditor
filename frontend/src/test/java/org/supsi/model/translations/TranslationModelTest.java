package org.supsi.model.translations;

import ch.supsi.application.preferences.PreferencesApplication;
import ch.supsi.application.translations.TranslationsApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TranslationModelTest {

    @Mock
    private TranslationsApplication mockTranslationApp;

    @Mock
    private PreferencesApplication mockPreferencesApp;

    private TranslationModel model;

    @BeforeEach
    public void setUp() throws Exception {
        Field instance = TranslationModel.class.getDeclaredField("myself");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    void testSingleton(){
        assertEquals(TranslationModel.getInstance(), TranslationModel.getInstance());
    }

    @Test
    void testGetLocale() {
        try (MockedStatic<TranslationsApplication> translationMock = mockStatic(TranslationsApplication.class);
             MockedStatic<PreferencesApplication> prefMock = mockStatic(PreferencesApplication.class)) {

            // Configura i mock statici
            translationMock.when(TranslationsApplication::getInstance).thenReturn(mockTranslationApp);
            prefMock.when(PreferencesApplication::getInstance).thenReturn(mockPreferencesApp);

            // Configura il comportamento del mock
            when(mockPreferencesApp.getPreference("language-tag"))
                    .thenReturn(Optional.of("en-US"));

            model = TranslationModel.getInstance();
            Locale locale = model.getLocale();

            assertNotNull(locale);
            assertEquals(Locale.forLanguageTag("en-US"), locale);
            verify(mockPreferencesApp).getPreference("language-tag");

            Locale locale1 = model.getLocale();

            assertEquals(locale, locale1);
        }
    }

    @Test
    void testGetUiBundle() throws IOException {
        ResourceBundle mockBundle = mock(ResourceBundle.class);

        try (MockedStatic<TranslationsApplication> translationMock = mockStatic(TranslationsApplication.class);
             MockedStatic<PreferencesApplication> prefMock = mockStatic(PreferencesApplication.class)) {

            translationMock.when(TranslationsApplication::getInstance).thenReturn(mockTranslationApp);
            prefMock.when(PreferencesApplication::getInstance).thenReturn(mockPreferencesApp);

            when(mockPreferencesApp.getPreference("language-tag"))
                    .thenReturn(Optional.of("en-US"));
            when(mockTranslationApp.getTranslationBundle(any(Locale.class)))
                    .thenReturn(Optional.of(mockBundle));

            model = TranslationModel.getInstance();
            ResourceBundle bundle = model.getUiBundle();

            assertNotNull(bundle);
            assertEquals(mockBundle, bundle);
            verify(mockTranslationApp).getTranslationBundle(any(Locale.class));
            verify(mockPreferencesApp).getPreference("language-tag");

            ResourceBundle bundle1 = model.getUiBundle();
            assertEquals(mockBundle, bundle1);
        }
    }

    @Test
    void testGetEmptyUiBundle() throws IOException {

        try (MockedStatic<TranslationsApplication> translationMock = mockStatic(TranslationsApplication.class);
             MockedStatic<PreferencesApplication> prefMock = mockStatic(PreferencesApplication.class)) {

            translationMock.when(TranslationsApplication::getInstance).thenReturn(mockTranslationApp);
            prefMock.when(PreferencesApplication::getInstance).thenReturn(mockPreferencesApp);

            when(mockPreferencesApp.getPreference("language-tag"))
                    .thenReturn(Optional.of("en-US"));
            when(mockTranslationApp.getTranslationBundle(any(Locale.class)))
                    .thenReturn(Optional.empty());

            model = TranslationModel.getInstance();
            IOException e = assertThrows(IOException.class, () ->model.getUiBundle());
            assertEquals("Translation bundle not found", e.getMessage());
        }
    }


    @Test
    void testTranslate() {
        String expectedTranslation = "Translated String";

        try (MockedStatic<TranslationsApplication> translationMock = mockStatic(TranslationsApplication.class);
             MockedStatic<PreferencesApplication> prefMock = mockStatic(PreferencesApplication.class)) {

            translationMock.when(TranslationsApplication::getInstance).thenReturn(mockTranslationApp);
            prefMock.when(PreferencesApplication::getInstance).thenReturn(mockPreferencesApp);

            when(mockTranslationApp.translate("test-string"))
                    .thenReturn(Optional.of(expectedTranslation));

            model = TranslationModel.getInstance();
            String translation = model.translate("test-string");

            assertNotNull(translation);
            assertEquals(expectedTranslation, translation);
            verify(mockTranslationApp).translate("test-string");
        }
    }

    @Test
    void testGetLocaleWithDefaultValue() {
        try (MockedStatic<TranslationsApplication> translationMock = mockStatic(TranslationsApplication.class);
             MockedStatic<PreferencesApplication> prefMock = mockStatic(PreferencesApplication.class)) {

            translationMock.when(TranslationsApplication::getInstance).thenReturn(mockTranslationApp);
            prefMock.when(PreferencesApplication::getInstance).thenReturn(mockPreferencesApp);

            when(mockPreferencesApp.getPreference("language-tag"))
                    .thenReturn(Optional.empty());

            model = TranslationModel.getInstance();
            Locale locale = model.getLocale();

            assertNotNull(locale);
            assertEquals(Locale.forLanguageTag("N/A"), locale);
            verify(mockPreferencesApp).getPreference("language-tag");
        }
    }
    @Test
    void testTranslateWithMissingTranslation() {
        try (MockedStatic<TranslationsApplication> translationMock = mockStatic(TranslationsApplication.class);
             MockedStatic<PreferencesApplication> prefMock = mockStatic(PreferencesApplication.class)) {

            translationMock.when(TranslationsApplication::getInstance).thenReturn(mockTranslationApp);
            prefMock.when(PreferencesApplication::getInstance).thenReturn(mockPreferencesApp);

            when(mockTranslationApp.translate("test-string"))
                    .thenReturn(Optional.empty());

            model = TranslationModel.getInstance();
            String translation = model.translate("test-string");

            assertEquals("Translation not found", translation);
            verify(mockTranslationApp).translate("test-string");
        }
    }
    @Test
    void testGetSupportedLanguages() {
        List<String> expectedLanguages = Arrays.asList("en-US", "it-IT");

        try (MockedStatic<TranslationsApplication> translationMock = mockStatic(TranslationsApplication.class);
             MockedStatic<PreferencesApplication> prefMock = mockStatic(PreferencesApplication.class)) {

            translationMock.when(TranslationsApplication::getInstance).thenReturn(mockTranslationApp);
            prefMock.when(PreferencesApplication::getInstance).thenReturn(mockPreferencesApp);

            when(mockTranslationApp.getSupportedLanguages())
                    .thenReturn(expectedLanguages);

            model = TranslationModel.getInstance();
            List<String> languages = model.getSupportedLanguages();

            assertEquals(expectedLanguages, languages);
            verify(mockTranslationApp).getSupportedLanguages();
        }
    }

    @Test
    void testGetCurrentLanguage() {
        try (MockedStatic<TranslationsApplication> translationMock = mockStatic(TranslationsApplication.class);
             MockedStatic<PreferencesApplication> prefMock = mockStatic(PreferencesApplication.class)) {

            translationMock.when(TranslationsApplication::getInstance).thenReturn(mockTranslationApp);
            prefMock.when(PreferencesApplication::getInstance).thenReturn(mockPreferencesApp);

            when(mockPreferencesApp.getPreference("language-tag"))
                    .thenReturn(Optional.of("en-US"));

            model = TranslationModel.getInstance();
            Locale currentLanguage =  model.getCurrentLanguage();

            assertNotNull(currentLanguage);
        }
    }
}