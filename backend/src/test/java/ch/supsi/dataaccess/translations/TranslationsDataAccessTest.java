package ch.supsi.dataaccess.translations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TranslationsDataAccessTest {

    private TranslationsDataAccess translationsDataAccess;

    @BeforeEach
    void setUp() {
        translationsDataAccess = TranslationsDataAccess.getInstance();
    }

    @Test
    void testGetInstanceSingleton() {
        TranslationsDataAccess instance1 = TranslationsDataAccess.getInstance();
        TranslationsDataAccess instance2 = TranslationsDataAccess.getInstance();

        assertSame(instance1, instance2, "TranslationsDataAccess should be a singleton");
    }

    @Test
    void testGetSupportedLanguages() {
        assertFalse(translationsDataAccess.getSupportedLanguageTags().isEmpty());
    }

    @Test
    void testGetSupportedLanguageTagsWithNullStream() throws IOException {
        TranslationsDataAccess translationsDataAccess = spy(TranslationsDataAccess.getInstance());
        doReturn(null).when(translationsDataAccess).getResourceAsStream(anyString());

        List<String> result = translationsDataAccess.getSupportedLanguageTags();

        assertTrue(result.isEmpty());
    }

    @Test
    void testFallbackAndMissingResource() {
        assertDoesNotThrow(() -> translationsDataAccess.getTranslations(Locale.FRENCH));
        assertFalse(translationsDataAccess.getTranslations(Locale.FRENCH).isEmpty());
    }

    @Test
    void testGetSupportedLanguageTagsWithException() throws IOException {
        TranslationsDataAccess translationsDataAccess = spy(TranslationsDataAccess.getInstance());
        doThrow(IOException.class).when(translationsDataAccess).getResourceAsStream(anyString());

        //should be caught
        assertDoesNotThrow(translationsDataAccess::getSupportedLanguageTags);
    }

    @Test
    void testGetSupportedLanguageTagsWithValidStream() throws IOException {
        TranslationsDataAccess translationsDataAccess = spy(TranslationsDataAccess.getInstance());
        Properties props = new Properties();
        props.setProperty("lang1", "en-US");
        props.setProperty("lang2", "it-CH");

        // Stream che contiene delle properties valide
        ByteArrayInputStream inputStream = new ByteArrayInputStream(
                "lang1=en-US\nlang2=it-CH".getBytes()
        );

        doReturn(inputStream).when(translationsDataAccess).getResourceAsStream(anyString());

        List<String> result = translationsDataAccess.getSupportedLanguageTags();

        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        assertTrue(result.contains("en-US"));
        assertTrue(result.contains("it-CH"));
    }

    @Test
    void testGetUiResourceBundle() {
        //should not find frontend module

        Locale locale = Locale.forLanguageTag(translationsDataAccess.getSupportedLanguageTags().get(0));
        Optional<ResourceBundle> bundle = translationsDataAccess.getUIResourceBundle(locale);
        assertTrue(bundle.isPresent());
        bundle.get().keySet().forEach(System.out::println);
        assertFalse(bundle.get().keySet().isEmpty()); //should contain fallback labels
    }

    @Test
    void testLoadFrontendResourcesFromJar() throws IOException {
        TranslationsDataAccess spy = spy(TranslationsDataAccess.getInstance());

        // Mock ModuleLayer.boot() per forzare il percorso JAR
        try (MockedStatic<ModuleLayer> moduleLayer = mockStatic(ModuleLayer.class)) {
            ModuleLayer mockLayer = mock(ModuleLayer.class);
            when(mockLayer.findModule("frontend")).thenReturn(Optional.empty());
            moduleLayer.when(ModuleLayer::boot).thenReturn(mockLayer);

            // Mock dello stream per il file properties
            ByteArrayInputStream inputStream = new ByteArrayInputStream(
                    "test.key=Test Value".getBytes()
            );
            doReturn(inputStream).when(spy).getClassLoaderResourceAsStream(anyString());

            Optional<ResourceBundle> bundles = spy.getUIResourceBundle(Locale.US);

            assertFalse(bundles.isEmpty());
        }
    }

    @Test
    void testLoadFrontendResourcesFromModule() throws IOException {
        TranslationsDataAccess spy = spy(TranslationsDataAccess.getInstance());

        // Mock ModuleLayer.boot() per simulare presenza modulo frontend
        try (MockedStatic<ModuleLayer> moduleLayer = mockStatic(ModuleLayer.class)) {
            Module mockModule = mock(Module.class);
            ModuleLayer mockLayer = mock(ModuleLayer.class);

            when(mockLayer.findModule("frontend")).thenReturn(Optional.of(mockModule));
            moduleLayer.when(ModuleLayer::boot).thenReturn(mockLayer);

            // Mock del path frontend
            ByteArrayInputStream configStream = new ByteArrayInputStream(
                    "frontend.labels.path=test/path".getBytes()
            );
            doReturn(configStream).when(spy).getResourceAsStream("/application.properties");

            // Mock dello stream per il file properties
            ByteArrayInputStream propsStream = new ByteArrayInputStream(
                    "test.key=Test Value".getBytes()
            );
            doReturn(propsStream).when(spy).getStreamFromModule(eq(mockModule), contains("test/path"));


            Optional<ResourceBundle> bundles = spy.getUIResourceBundle(Locale.US);

            assertFalse(bundles.isEmpty());
        }
    }

    @Test
    void testLoadFrontendResourcesWithNull() throws IOException {
        TranslationsDataAccess spy = spy(TranslationsDataAccess.getInstance());

        ByteArrayInputStream supportedLanguagesStream = new ByteArrayInputStream(
                "default=en-US".getBytes()
        );
        doReturn(supportedLanguagesStream)
                .when(spy)
                .getResourceAsStream("/supported-languages.properties");

        try (MockedStatic<ModuleLayer> moduleLayer = mockStatic(ModuleLayer.class)) {
            ModuleLayer mockLayer = mock(ModuleLayer.class);
            when(mockLayer.findModule("frontend")).thenReturn(Optional.empty());
            moduleLayer.when(ModuleLayer::boot).thenReturn(mockLayer);

            doReturn(null)
                    .when(spy)
                    .getResourceAsStream(argThat(path -> !path.equals("/supported-languages.properties")));

            Optional<ResourceBundle> bundles = spy.getUIResourceBundle(Locale.US);

            assertFalse(bundles.isEmpty()); //should use missing resource bundle and fallback
        }
    }

    @Test
    void testLoadFrontendResourcesWithIOException() throws IOException {
        TranslationsDataAccess spy = spy(TranslationsDataAccess.getInstance());

        ByteArrayInputStream supportedLanguagesStream = new ByteArrayInputStream(
                "default=en-US".getBytes()
        );
        doReturn(supportedLanguagesStream)
                .when(spy)
                .getResourceAsStream("/supported-languages.properties");

        try (MockedStatic<ModuleLayer> moduleLayer = mockStatic(ModuleLayer.class)) {
            ModuleLayer mockLayer = mock(ModuleLayer.class);
            when(mockLayer.findModule("frontend")).thenReturn(Optional.empty());
            moduleLayer.when(ModuleLayer::boot).thenReturn(mockLayer);

            doThrow(new IOException("Test exception"))
                    .when(spy)
                    .getClassLoaderResourceAsStream(argThat(path -> !path.equals("/supported-languages.properties")));

            Optional<ResourceBundle> bundles = spy.getUIResourceBundle(Locale.US);

            assertFalse(bundles.isEmpty()); //should use missing resource bundle and fallback
        }
    }

    @Test
    void testLoadFrontendResourcesWithValidApplicationProperties() throws IOException {
        TranslationsDataAccess spy = spy(TranslationsDataAccess.getInstance());
        // mock delle lingue supportate
        ByteArrayInputStream supportedLanguagesStream = new ByteArrayInputStream(
                "l1=en-US".getBytes()
        );
        doReturn(supportedLanguagesStream)
                .when(spy)
                .getResourceAsStream("/supported-languages.properties");

        try (MockedStatic<ModuleLayer> moduleLayer = mockStatic(ModuleLayer.class)) {
            // mock modulo presente
            ModuleLayer mockLayer = mock(ModuleLayer.class);
            Module mockModule = mock(Module.class);
            when(mockLayer.findModule("frontend")).thenReturn(Optional.of(mockModule));
            moduleLayer.when(ModuleLayer::boot).thenReturn(mockLayer);

            // mock application.properties valido
            ByteArrayInputStream appPropsStream = new ByteArrayInputStream(
                    "frontend.labels.path=/custom/path".getBytes()
            );
            doReturn(appPropsStream)
                    .when(spy)
                    .getResourceAsStream("/application.properties");

            // mock dei labels
            ByteArrayInputStream labelsStream = new ByteArrayInputStream(
                    "test.key=Test Value".getBytes()
            );
            doReturn(labelsStream)
                    .when(spy)
                    .getResourceAsStream("/custom/path_en-US.properties");

            Optional<ResourceBundle> bundles = spy.getUIResourceBundle(Locale.US);
            assertFalse(bundles.isEmpty()); //should use missing resource bundle and fallback
        }
    }

    @Test
    void testLoadFrontendResourcesWithMissingApplicationProperties() throws IOException {
        TranslationsDataAccess spy = spy(TranslationsDataAccess.getInstance());

        // mock delle lingue supportate
        ByteArrayInputStream supportedLanguagesStream = new ByteArrayInputStream(
                "l1=en-US".getBytes()
        );
        doReturn(supportedLanguagesStream)
                .when(spy)
                .getResourceAsStream("/supported-languages.properties");

        try (MockedStatic<ModuleLayer> moduleLayer = mockStatic(ModuleLayer.class)) {
            // mock modulo presente
            ModuleLayer mockLayer = mock(ModuleLayer.class);
            Module mockModule = mock(Module.class);
            when(mockLayer.findModule("frontend")).thenReturn(Optional.of(mockModule));
            moduleLayer.when(ModuleLayer::boot).thenReturn(mockLayer);

            // mock application.properties mancante
            doReturn(null)
                    .when(spy)
                    .getResourceAsStream("/application.properties");

            Optional<ResourceBundle> bundles = spy.getUIResourceBundle(Locale.US);
            assertFalse(bundles.isEmpty()); //should use missing resource bundle and fallback
        }
    }

    @Test
    void testLoadFrontendResourcesWithApplicationPropertiesIOException() throws IOException {
        TranslationsDataAccess spy = spy(TranslationsDataAccess.getInstance());

        // mock delle lingue supportate
        ByteArrayInputStream supportedLanguagesStream = new ByteArrayInputStream(
                "default=en-US".getBytes()
        );
        doReturn(supportedLanguagesStream)
                .when(spy)
                .getResourceAsStream("/supported-languages.properties");

        try (MockedStatic<ModuleLayer> moduleLayer = mockStatic(ModuleLayer.class)) {
            // mock modulo presente
            ModuleLayer mockLayer = mock(ModuleLayer.class);
            Module mockModule = mock(Module.class);
            when(mockLayer.findModule("frontend")).thenReturn(Optional.of(mockModule));
            moduleLayer.when(ModuleLayer::boot).thenReturn(mockLayer);

            // mock IOException su application.properties
            doThrow(new IOException("Test exception"))
                    .when(spy)
                    .getResourceAsStream("/application.properties");

            Optional<ResourceBundle> bundles = spy.getUIResourceBundle(Locale.US);
            assertFalse(bundles.isEmpty()); //should use missing resource bundle and fallback
        }
    }

    @Test
    void testLoadBackendResourcesWithIOException() throws IOException {
        TranslationsDataAccess spy = spy(TranslationsDataAccess.getInstance());

        PathMatchingResourcePatternResolver mockResolver = mock(PathMatchingResourcePatternResolver.class);
        when(mockResolver.getResources(anyString()))
                .thenThrow(new IOException("Test exception"));

        // inietto il mock resolver
        doReturn(mockResolver)
                .when(spy)
                .createResourcePatternResolver();

        List<ResourceBundle> bundles = spy.loadBackendResources(Locale.US, "test/path");

        assertTrue(bundles.isEmpty()); //should handle missing resource and try to use backend labels
        //but since we are mocking backend resources it should be empty
    }

    @Test
    void testLoadBackendResourcesWithNullFilename() throws IOException {
        TranslationsDataAccess spy = spy(TranslationsDataAccess.getInstance());

        Resource mockResource = mock(Resource.class);
        when(mockResource.getFilename()).thenReturn(null);

        PathMatchingResourcePatternResolver mockResolver = mock(PathMatchingResourcePatternResolver.class);
        when(mockResolver.getResources(anyString()))
                .thenReturn(new Resource[]{mockResource});

        doReturn(mockResolver)
                .when(spy)
                .createResourcePatternResolver();

        List<ResourceBundle> bundles = spy.loadBackendResources(Locale.US, "test/path");

        assertTrue(bundles.isEmpty()); //should handle missing resource and try to use backend labels
        //but since we are mocking backend resources it should be empty
    }
}
