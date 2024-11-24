package org.supsi.controller.about;

import javafx.fxml.FXMLLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.supsi.model.about.AboutModel;
import org.supsi.model.about.IAboutModel;
import org.supsi.model.info.LoggerModel;
import org.supsi.model.translations.TranslationModel;
import org.supsi.view.IView;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AboutControllerTest {

    @Mock
    private IView<IAboutModel> mockView;

    @Mock
    private ResourceBundle mockBundle;

    private AboutController controller;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        Field instanceField = AboutController.class.getDeclaredField("myself");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
    }

    @Test
    void testShowPopup() throws Exception {
        try (MockedStatic<TranslationModel> translationModelMock = mockStatic(TranslationModel.class);
             MockedStatic<AboutModel> aboutModelMock = mockStatic(AboutModel.class);
             MockedStatic<LoggerModel> loggerModelMock = mockStatic(LoggerModel.class);
             MockedConstruction<FXMLLoader> fxmlLoaderMock = mockConstruction(FXMLLoader.class,
                     (mock, context) -> {
                         when(mock.load()).thenReturn(null);
                         when(mock.getController()).thenReturn(mockView);
                     })) {

            TranslationModel mockTranslationModel = mock(TranslationModel.class);
            AboutModel mockAboutModel = mock(AboutModel.class);
            LoggerModel mockLoggerModel = mock(LoggerModel.class);

            when(mockTranslationModel.getUiBundle()).thenReturn(mockBundle);
            when(mockTranslationModel.getLocale()).thenReturn(Locale.ITALY);

            translationModelMock.when(TranslationModel::getInstance).thenReturn(mockTranslationModel);
            aboutModelMock.when(AboutModel::getInstance).thenReturn(mockAboutModel);
            loggerModelMock.when(LoggerModel::getInstance).thenReturn(mockLoggerModel);


            controller = AboutController.getInstance();
            controller.showPopup();

            verify(mockLoggerModel).addDebug("ui_start_popup_build");
            verify(mockView).build();
            verify(mockLoggerModel).addDebug("ui_end_popup_build");
            verify(mockView).show();
            verify(mockLoggerModel).addDebug("ui_popup_show");
        }
    }

    @Test
    void testSingleton() throws IOException {
        try (MockedStatic<TranslationModel> translationModelMock = mockStatic(TranslationModel.class);
             MockedStatic<AboutModel> aboutModelMock = mockStatic(AboutModel.class);
             MockedStatic<LoggerModel> loggerModelMock = mockStatic(LoggerModel.class);
             MockedConstruction<FXMLLoader> fxmlLoaderMock = mockConstruction(FXMLLoader.class,
                     (mock, context) -> {
                         when(mock.load()).thenReturn(null);
                         when(mock.getController()).thenReturn(mockView);
                     })) {

            TranslationModel mockTranslationModel = mock(TranslationModel.class);
            AboutModel mockAboutModel = mock(AboutModel.class);
            LoggerModel mockLoggerModel = mock(LoggerModel.class);


            when(mockTranslationModel.getUiBundle()).thenReturn(mockBundle);
            when(mockTranslationModel.getLocale()).thenReturn(Locale.ITALY);

            translationModelMock.when(TranslationModel::getInstance).thenReturn(mockTranslationModel);
            aboutModelMock.when(AboutModel::getInstance).thenReturn(mockAboutModel);
            loggerModelMock.when(LoggerModel::getInstance).thenReturn(mockLoggerModel);

            AboutController first = AboutController.getInstance();
            AboutController second = AboutController.getInstance();

            assertSame(first, second);
        }
    }

    @Test
    void testInitializationWithIOException() throws IOException {
        try (MockedStatic<TranslationModel> translationModelMock = mockStatic(TranslationModel.class);
             MockedStatic<AboutModel> aboutModelMock = mockStatic(AboutModel.class);
             MockedStatic<LoggerModel> loggerModelMock = mockStatic(LoggerModel.class);
             MockedConstruction<FXMLLoader> dummy = mockConstruction(FXMLLoader.class,
                     (mock, context) -> {
                         when(mock.load()).thenThrow(new IOException("Test exception"));
                     })) {

            TranslationModel mockTranslationModel = mock(TranslationModel.class);
            AboutModel mockAboutModel = mock(AboutModel.class);
            LoggerModel mockLoggerModel = mock(LoggerModel.class);


            when(mockTranslationModel.getUiBundle()).thenReturn(mockBundle);
            when(mockTranslationModel.getLocale()).thenReturn(Locale.ITALY);

            translationModelMock.when(TranslationModel::getInstance).thenReturn(mockTranslationModel);
            aboutModelMock.when(AboutModel::getInstance).thenReturn(mockAboutModel);
            loggerModelMock.when(LoggerModel::getInstance).thenReturn(mockLoggerModel);

            controller = new AboutController();

            verify(mockLoggerModel).addError("ui_failed_to_load_component");
        }
    }
    @Test
    void testReadBuildInfoSuccess() throws Exception {
        //sample content
        String propertiesContent = """
            build-time=2024-03-21T10:00:00
            build-version=1.0.0
            developer=Test Developer
            """;
        InputStream mockStream = new ByteArrayInputStream(propertiesContent.getBytes());

        try (MockedStatic<TranslationModel> translationModelMock = mockStatic(TranslationModel.class);
             MockedStatic<AboutModel> aboutModelMock = mockStatic(AboutModel.class);
             MockedStatic<LoggerModel> loggerModelMock = mockStatic(LoggerModel.class);
             MockedConstruction<FXMLLoader> fxmlLoaderMock = mockConstruction(FXMLLoader.class,
                     (mock, context) -> {
                         when(mock.load()).thenReturn(null);
                         when(mock.getController()).thenReturn(mockView);
                     })) {

            TranslationModel mockTranslationModel = mock(TranslationModel.class);
            AboutModel mockAboutModel = mock(AboutModel.class);
            LoggerModel mockLoggerModel = mock(LoggerModel.class);

            when(mockTranslationModel.getUiBundle()).thenReturn(mockBundle);
            when(mockTranslationModel.getLocale()).thenReturn(Locale.ITALY);

            translationModelMock.when(TranslationModel::getInstance).thenReturn(mockTranslationModel);
            aboutModelMock.when(AboutModel::getInstance).thenReturn(mockAboutModel);
            loggerModelMock.when(LoggerModel::getInstance).thenReturn(mockLoggerModel);

            //sottoclasse per mockm
            AboutController controllerSpy = spy(new AboutController() {
                @Override
                public InputStream getResourceAsStream(String name) {
                    return mockStream;
                }
            });

            verify(mockAboutModel).setVersion("1.0.0");
            verify(mockAboutModel).setDeveloper("Test Developer");
            verify(mockLoggerModel).addDebug("ui_build_properties_parsed");
        }
    }

    @Test
    void testReadBuildInfoWithParseError() throws Exception {
        String propertiesContent = """
            build-time=invalid-date
            build-version=1.0.0
            developer=Test Developer
            """;
        InputStream mockStream = new ByteArrayInputStream(propertiesContent.getBytes());

        try (MockedStatic<TranslationModel> translationModelMock = mockStatic(TranslationModel.class);
             MockedStatic<AboutModel> aboutModelMock = mockStatic(AboutModel.class);
             MockedStatic<LoggerModel> loggerModelMock = mockStatic(LoggerModel.class);
             MockedConstruction<FXMLLoader> fxmlLoaderMock = mockConstruction(FXMLLoader.class,
                     (mock, context) -> {
                         when(mock.load()).thenReturn(null);
                         when(mock.getController()).thenReturn(mockView);
                     })) {

            TranslationModel mockTranslationModel = mock(TranslationModel.class);
            AboutModel mockAboutModel = mock(AboutModel.class);
            LoggerModel mockLoggerModel = mock(LoggerModel.class);

            when(mockTranslationModel.getUiBundle()).thenReturn(mockBundle);
            when(mockTranslationModel.getLocale()).thenReturn(Locale.ITALY);

            translationModelMock.when(TranslationModel::getInstance).thenReturn(mockTranslationModel);
            aboutModelMock.when(AboutModel::getInstance).thenReturn(mockAboutModel);
            loggerModelMock.when(LoggerModel::getInstance).thenReturn(mockLoggerModel);

            AboutController controllerSpy = spy(new AboutController() {
                @Override
                public InputStream getResourceAsStream(String name) {
                    return mockStream;
                }
            });

            verify(mockLoggerModel).addDebug("ui_build_properties_date_parse_error");
            verify(mockAboutModel).setDate("N/A");
        }
    }

    @Test
    void testInitializationWithMissingFXML() {
        try (MockedStatic<TranslationModel> translationModelMock = mockStatic(TranslationModel.class);
             MockedStatic<AboutModel> aboutModelMock = mockStatic(AboutModel.class);
             MockedStatic<LoggerModel> loggerModelMock = mockStatic(LoggerModel.class)) {

            TranslationModel mockTranslationModel = mock(TranslationModel.class);
            AboutModel mockAboutModel = mock(AboutModel.class);
            LoggerModel mockLoggerModel = mock(LoggerModel.class);

            translationModelMock.when(TranslationModel::getInstance).thenReturn(mockTranslationModel);
            aboutModelMock.when(AboutModel::getInstance).thenReturn(mockAboutModel);
            loggerModelMock.when(LoggerModel::getInstance).thenReturn(mockLoggerModel);

            AboutController controllerSpy = spy(new AboutController() {
                @Override
                public URL getResource(String name) {
                    return null;
                }
            });

            verify(mockLoggerModel, never()).addError(anyString());
            verify(mockLoggerModel, never()).addDebug("ui_about_loaded");
        }
    }

    @Test
    void testReadBuildInfoWithMissingFile() throws Exception {
        try (MockedStatic<TranslationModel> translationModelMock = mockStatic(TranslationModel.class);
             MockedStatic<AboutModel> aboutModelMock = mockStatic(AboutModel.class);
             MockedStatic<LoggerModel> loggerModelMock = mockStatic(LoggerModel.class);
             MockedConstruction<FXMLLoader> fxmlLoaderMock = mockConstruction(FXMLLoader.class,
                     (mock, context) -> {
                         when(mock.load()).thenReturn(null);
                         when(mock.getController()).thenReturn(mockView);
                     })) {

            TranslationModel mockTranslationModel = mock(TranslationModel.class);
            AboutModel mockAboutModel = mock(AboutModel.class);
            LoggerModel mockLoggerModel = mock(LoggerModel.class);

            when(mockTranslationModel.getUiBundle()).thenReturn(mockBundle);

            translationModelMock.when(TranslationModel::getInstance).thenReturn(mockTranslationModel);
            aboutModelMock.when(AboutModel::getInstance).thenReturn(mockAboutModel);
            loggerModelMock.when(LoggerModel::getInstance).thenReturn(mockLoggerModel);

            // sottoclasse x mock
            AboutController controllerSpy = spy(new AboutController() {
                @Override
                public InputStream getResourceAsStream(String name) {
                    return null; // sim imulo file mancante
                }
            });

            verify(mockLoggerModel).addDebug("ui_build_properties_not_found");
        }
    }

    @Test
    void testReadBuildInfoWithIOException() throws Exception {
        try (MockedStatic<TranslationModel> translationModelMock = mockStatic(TranslationModel.class);
             MockedStatic<AboutModel> aboutModelMock = mockStatic(AboutModel.class);
             MockedStatic<LoggerModel> loggerModelMock = mockStatic(LoggerModel.class);
             MockedConstruction<FXMLLoader> fxmlLoaderMock = mockConstruction(FXMLLoader.class,
                     (mock, context) -> {
                         when(mock.load()).thenReturn(null);
                         when(mock.getController()).thenReturn(mockView);
                     })) {

            TranslationModel mockTranslationModel = mock(TranslationModel.class);
            AboutModel mockAboutModel = mock(AboutModel.class);
            LoggerModel mockLoggerModel = mock(LoggerModel.class);

            when(mockTranslationModel.getUiBundle()).thenReturn(mockBundle);

            translationModelMock.when(TranslationModel::getInstance).thenReturn(mockTranslationModel);
            aboutModelMock.when(AboutModel::getInstance).thenReturn(mockAboutModel);
            loggerModelMock.when(LoggerModel::getInstance).thenReturn(mockLoggerModel);

            AboutController controllerSpy = spy(new AboutController() {
                @Override
                public InputStream getResourceAsStream(String name) throws IOException {
                    throw new IOException("Test exception");
                }
            });

            verify(mockLoggerModel).addDebug("ui_build_properties_error");
        }
    }
}