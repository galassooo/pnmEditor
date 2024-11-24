package org.supsi.controller.error;

import javafx.fxml.FXMLLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.supsi.controller.about.AboutController;
import org.supsi.controller.errors.ErrorController;
import org.supsi.model.errors.ErrorModel;
import org.supsi.model.errors.IErrorModel;
import org.supsi.model.info.LoggerModel;
import org.supsi.model.translations.TranslationModel;
import org.supsi.view.IView;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class ErrorControllerTest {

    @Mock
    private LoggerModel loggerModel;

    @Mock
    private ErrorModel errorModel;

    @Mock
    private IView<IErrorModel> view;

    @Mock
    private ResourceBundle mockBundle;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        var myselfField = ErrorController.class.getDeclaredField("mySelf");
        myselfField.setAccessible(true);
        myselfField.set(null, null);
    }


    @Test
    void testGetInstanceSingleton() throws Exception {
        try (MockedStatic<ErrorModel> errorModelMock = mockStatic(ErrorModel.class);
             MockedStatic<LoggerModel> loggerModelMock = mockStatic(LoggerModel.class);
             MockedConstruction<FXMLLoader> fxmlLoaderMock = mockConstruction(FXMLLoader.class,
                     (mockLoader, context) -> {
                         when(mockLoader.load()).thenReturn(null);
                         when(mockLoader.getController()).thenReturn(view);
                     })) {


            errorModelMock.when(ErrorModel::getInstance).thenReturn(errorModel);
            loggerModelMock.when(LoggerModel::getInstance).thenReturn(loggerModel);


            var myselfField = ErrorController.class.getDeclaredField("mySelf");
            myselfField.setAccessible(true);
            myselfField.set(null, null);

            ErrorController firstInstance = ErrorController.getInstance();

            //test
            ErrorController secondInstance = ErrorController.getInstance();
            assertEquals(firstInstance, secondInstance);
        }
    }

    @Test
    void testShowErrorDisplaysPopup() throws Exception {
        try (MockedStatic<ErrorModel> errorModelMock = mockStatic(ErrorModel.class);
             MockedStatic<LoggerModel> loggerModelMock = mockStatic(LoggerModel.class);
             MockedStatic<TranslationModel> translationModelMock = mockStatic(TranslationModel.class);
             MockedConstruction<FXMLLoader> fxmlLoaderMock = mockConstruction(FXMLLoader.class,
                     (mockLoader, context) -> {
                         when(mockLoader.load()).thenReturn(null);
                         when(mockLoader.getController()).thenReturn(view);
                     })) {

            TranslationModel mockTranslationModel = mock(TranslationModel.class);

            errorModelMock.when(ErrorModel::getInstance).thenReturn(errorModel);
            loggerModelMock.when(LoggerModel::getInstance).thenReturn(loggerModel);
            translationModelMock.when(TranslationModel::getInstance).thenReturn(mockTranslationModel);

            when(mockTranslationModel.getUiBundle()).thenReturn(mockBundle);
            ErrorController controller = ErrorController.getInstance();

            String testMessage = "Test error message";
            controller.showError(testMessage);

            // Verify logger interactions
            verify(loggerModel).addError(testMessage);
            verify(loggerModel).addDebug("ui_start_popup_build");
            verify(loggerModel).addDebug("ui_end_popup_build");
            verify(loggerModel).addDebug("ui_popup_show");

            // Verify model and view interactions
            verify(errorModel).setMessage(testMessage);
            IView<IErrorModel> mockView = fxmlLoaderMock.constructed().get(0).getController();
            verify(mockView).build();
            verify(mockView).show();
        }
    }

    @Test
    void testFXMLLoadNullURL(){
        try (MockedStatic<ErrorModel> errorModelMock = mockStatic(ErrorModel.class);
             MockedStatic<LoggerModel> loggerModelMock = mockStatic(LoggerModel.class);
             MockedConstruction<FXMLLoader> fxmlLoaderMock = mockConstruction(FXMLLoader.class,
                     (mockLoader, context) -> {
                         when(mockLoader.load()).thenReturn(null);
                         when(mockLoader.getController()).thenReturn(view);
                     })) {


            errorModelMock.when(ErrorModel::getInstance).thenReturn(errorModel);
            loggerModelMock.when(LoggerModel::getInstance).thenReturn(loggerModel);

            ErrorController errorCtrl = spy(new ErrorController() {
                @Override
                public URL getResource(String name) {
                    return null;
                }
            });
        }
        verify(loggerModel, never()).addError(anyString());
        verify(loggerModel, never()).addDebug("ui_error_loaded");
        verify(loggerModel, never()).addDebug("ui_failed_to_load_component");
    }
    @Test
    void testFXMLLoadFailure() throws Exception {
        try (MockedStatic<LoggerModel> loggerModelMock = mockStatic(LoggerModel.class);
             MockedConstruction<FXMLLoader> fxmlLoaderMock = mockConstruction(FXMLLoader.class,
                     (mockLoader, context) -> {
                         when(mockLoader.load()).thenThrow(new IOException("Test IOException"));
                     })) {

            LoggerModel mockLoggerModel = mock(LoggerModel.class);
            loggerModelMock.when(LoggerModel::getInstance).thenReturn(mockLoggerModel);

            ErrorController controller = ErrorController.getInstance();

            verify(mockLoggerModel).addError("ui_failed_to_load_component");
        }
    }
}
