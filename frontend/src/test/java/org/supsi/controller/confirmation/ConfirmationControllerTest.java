package org.supsi.controller.confirmation;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXMLLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.supsi.model.info.LoggerModel;
import org.supsi.model.state.StateModel;
import org.supsi.model.translations.TranslationModel;
import org.supsi.view.info.IConfirmPopup;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class ConfirmationControllerTest {

    @Mock
    private IConfirmPopup mockView;

    @Mock
    private ResourceBundle mockBundle;

    @Mock
    private Consumer<?> mockCallback;

    private SimpleBooleanProperty changesPendingProperty;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        // Reset singleton
        Field instanceField = ConfirmationController.class.getDeclaredField("myself");
        instanceField.setAccessible(true);
        instanceField.set(null, null);

        changesPendingProperty = new SimpleBooleanProperty(false);
    }

    @Test
    void testInitializationSuccess() throws IOException {
        try (MockedStatic<StateModel> stateModelMock = mockStatic(StateModel.class);
             MockedStatic<TranslationModel> translationModelMock = mockStatic(TranslationModel.class);
             MockedStatic<LoggerModel> loggerModelMock = mockStatic(LoggerModel.class);
             MockedConstruction<FXMLLoader> fxmlLoaderMock = mockConstruction(FXMLLoader.class,
                     (mock, context) -> {
                         when(mock.load()).thenReturn(null);
                         when(mock.getController()).thenReturn(mockView);
                     })) {

            // Setup mocks
            StateModel mockStateModel = mock(StateModel.class);
            TranslationModel mockTranslationModel = mock(TranslationModel.class);
            LoggerModel mockLoggerModel = mock(LoggerModel.class);
            URL mockUrl = new URL("file://test.fxml");

            when(mockStateModel.areChangesPending()).thenReturn(changesPendingProperty);
            when(mockTranslationModel.getUiBundle()).thenReturn(mockBundle);

            stateModelMock.when(StateModel::getInstance).thenReturn(mockStateModel);
            translationModelMock.when(TranslationModel::getInstance).thenReturn(mockTranslationModel);
            loggerModelMock.when(LoggerModel::getInstance).thenReturn(mockLoggerModel);

            // Create controller with mocked getResource
            ConfirmationController controllerSpy = spy(new ConfirmationController() {
                @Override
                public URL getResource(String name) {
                    return mockUrl;
                }
            });

            verify(mockLoggerModel).addDebug("ui__loaded");
        }
    }

    @Test
    void testInitializationNullURL() throws IOException {
        try (MockedStatic<StateModel> stateModelMock = mockStatic(StateModel.class);
             MockedStatic<TranslationModel> translationModelMock = mockStatic(TranslationModel.class);
             MockedStatic<LoggerModel> loggerModelMock = mockStatic(LoggerModel.class);
             MockedConstruction<FXMLLoader> fxmlLoaderMock = mockConstruction(FXMLLoader.class,
                     (mock, context) -> {
                         when(mock.load()).thenReturn(null);
                         when(mock.getController()).thenReturn(mockView);
                     })) {

            // Setup mocks
            StateModel mockStateModel = mock(StateModel.class);
            TranslationModel mockTranslationModel = mock(TranslationModel.class);
            LoggerModel mockLoggerModel = mock(LoggerModel.class);
            URL mockUrl = null;

            when(mockStateModel.areChangesPending()).thenReturn(changesPendingProperty);
            when(mockTranslationModel.getUiBundle()).thenReturn(mockBundle);

            stateModelMock.when(StateModel::getInstance).thenReturn(mockStateModel);
            translationModelMock.when(TranslationModel::getInstance).thenReturn(mockTranslationModel);
            loggerModelMock.when(LoggerModel::getInstance).thenReturn(mockLoggerModel);

            // Create controller with mocked getResource
            ConfirmationController controllerSpy = spy(new ConfirmationController() {
                @Override
                public URL getResource(String name) {
                    return mockUrl;
                }
            });
            assertDoesNotThrow(ConfirmationController::getInstance);
        }
    }

    @Test
    void testInitializationWithIOException() throws IOException {
        try (MockedStatic<StateModel> stateModelMock = mockStatic(StateModel.class);
             MockedStatic<TranslationModel> translationModelMock = mockStatic(TranslationModel.class);
             MockedStatic<LoggerModel> loggerModelMock = mockStatic(LoggerModel.class);
             MockedConstruction<FXMLLoader> fxmlLoaderMock = mockConstruction(FXMLLoader.class,
                     (mock, context) -> {
                         when(mock.load()).thenThrow(new IOException("Test exception"));
                     })) {

            // Setup mocks
            StateModel mockStateModel = mock(StateModel.class);
            TranslationModel mockTranslationModel = mock(TranslationModel.class);
            LoggerModel mockLoggerModel = mock(LoggerModel.class);
            URL mockUrl = new URL("file://test.fxml");

            when(mockStateModel.areChangesPending()).thenReturn(changesPendingProperty);
            when(mockTranslationModel.getUiBundle()).thenReturn(mockBundle);

            stateModelMock.when(StateModel::getInstance).thenReturn(mockStateModel);
            translationModelMock.when(TranslationModel::getInstance).thenReturn(mockTranslationModel);
            loggerModelMock.when(LoggerModel::getInstance).thenReturn(mockLoggerModel);

            // Create controller with mocked getResource
            ConfirmationController controllerSpy = spy(new ConfirmationController() {
                @Override
                public URL getResource(String name) {
                    return mockUrl;
                }
            });

            verify(mockLoggerModel).addError("ui_failed_to_load_component");
        }
    }

    @Test
    void testRequestConfirmWhenChangesPendingAndUserDenies() throws Exception {
        // Creazione dei mock statici
        try (MockedStatic<StateModel> stateModelMock = mockStatic(StateModel.class);
             MockedStatic<TranslationModel> translationModelMock = mockStatic(TranslationModel.class);
             MockedStatic<LoggerModel> loggerModelMock = mockStatic(LoggerModel.class);
             MockedConstruction<FXMLLoader> fxmlLoaderMock = mockConstruction(FXMLLoader.class,
                     (mockLoader, context) -> {
                         when(mockLoader.load()).thenReturn(null);
                         when(mockLoader.getController()).thenReturn(mockView); //x mockare la biew
                     })) {


            StateModel mockStateModel = mock(StateModel.class);
            TranslationModel mockTranslationModel = mock(TranslationModel.class);
            LoggerModel mockLoggerModel = mock(LoggerModel.class);

            // setto property
            BooleanProperty changesPendingProperty = new SimpleBooleanProperty(false);
            when(mockStateModel.areChangesPending()).thenReturn(changesPendingProperty);

            when(mockTranslationModel.getUiBundle()).thenReturn(mockBundle);
            when(mockView.showConfirmationDialog()).thenReturn(false);

            stateModelMock.when(StateModel::getInstance).thenReturn(mockStateModel);
            translationModelMock.when(TranslationModel::getInstance).thenReturn(mockTranslationModel);
            loggerModelMock.when(LoggerModel::getInstance).thenReturn(mockLoggerModel);

            //DOPO mocks -> init ctrl
            ConfirmationController controller = ConfirmationController.getInstance();

            //aggiorno stato prop e ctrl viene notificato
            changesPendingProperty.set(true);


            controller.requestConfirm(mockCallback);

            //verifica
            verify(mockView).showConfirmationDialog();
            verify(mockCallback, never()).accept(any());
        }
    }

    @Test
    void testRequestConfirmWhenChangesPendingAndUserConfirms() throws Exception {
        // Creazione dei mock statici
        try (MockedStatic<StateModel> stateModelMock = mockStatic(StateModel.class);
             MockedStatic<TranslationModel> translationModelMock = mockStatic(TranslationModel.class);
             MockedStatic<LoggerModel> loggerModelMock = mockStatic(LoggerModel.class);
             MockedConstruction<FXMLLoader> fxmlLoaderMock = mockConstruction(FXMLLoader.class,
                     (mockLoader, context) -> {
                         when(mockLoader.load()).thenReturn(null);
                         when(mockLoader.getController()).thenReturn(mockView); //x mockare la biew
                     })) {


            StateModel mockStateModel = mock(StateModel.class);
            TranslationModel mockTranslationModel = mock(TranslationModel.class);
            LoggerModel mockLoggerModel = mock(LoggerModel.class);

            // setto property
            BooleanProperty changesPendingProperty = new SimpleBooleanProperty(false);
            when(mockStateModel.areChangesPending()).thenReturn(changesPendingProperty);

            when(mockTranslationModel.getUiBundle()).thenReturn(mockBundle);
            when(mockView.showConfirmationDialog()).thenReturn(true);

            stateModelMock.when(StateModel::getInstance).thenReturn(mockStateModel);
            translationModelMock.when(TranslationModel::getInstance).thenReturn(mockTranslationModel);
            loggerModelMock.when(LoggerModel::getInstance).thenReturn(mockLoggerModel);

            //DOPO mocks -> init ctrl
            ConfirmationController controller = ConfirmationController.getInstance();

            //aggiorno stato prop e ctrl viene notificato
            changesPendingProperty.set(true);


            controller.requestConfirm(mockCallback);

            //verifica
            verify(mockView).showConfirmationDialog();
            verify(mockCallback).accept(any());
        }
    }

    @Test
    void testRequestConfirmWhenNoChangesPending() throws IOException {
        try (MockedStatic<StateModel> stateModelMock = mockStatic(StateModel.class);
             MockedStatic<TranslationModel> translationModelMock = mockStatic(TranslationModel.class);
             MockedStatic<LoggerModel> loggerModelMock = mockStatic(LoggerModel.class);
             MockedConstruction<FXMLLoader> fxmlLoaderMock = mockConstruction(FXMLLoader.class,
                     (mock, context) -> {
                         when(mock.load()).thenReturn(null);
                         when(mock.getController()).thenReturn(mockView);
                     })) {

            // Setup mocks
            StateModel mockStateModel = mock(StateModel.class);
            TranslationModel mockTranslationModel = mock(TranslationModel.class);
            LoggerModel mockLoggerModel = mock(LoggerModel.class);
            URL mockUrl = new URL("file://test.fxml");

            changesPendingProperty.set(false);
            when(mockStateModel.areChangesPending()).thenReturn(changesPendingProperty);
            when(mockTranslationModel.getUiBundle()).thenReturn(mockBundle);

            stateModelMock.when(StateModel::getInstance).thenReturn(mockStateModel);
            translationModelMock.when(TranslationModel::getInstance).thenReturn(mockTranslationModel);
            loggerModelMock.when(LoggerModel::getInstance).thenReturn(mockLoggerModel);

            ConfirmationController controller = ConfirmationController.getInstance();
            controller.requestConfirm(mockCallback);

            verify(mockView, never()).showConfirmationDialog();
            verify(mockCallback).accept(null);
        }
    }

    @Test
    void testSingleton() throws Exception {
        try (MockedStatic<StateModel> stateModelMock = mockStatic(StateModel.class);
             MockedStatic<TranslationModel> translationModelMock = mockStatic(TranslationModel.class);
             MockedStatic<LoggerModel> loggerModelMock = mockStatic(LoggerModel.class);
             MockedConstruction<FXMLLoader> fxmlLoaderMock = mockConstruction(FXMLLoader.class,
                     (mockLoader, context) -> { //CHIAMATO DAL COSTRUTTORE
                         when(mockLoader.load()).thenReturn(null);
                         when(mockLoader.getController()).thenReturn(mock(IConfirmPopup.class));
                     })) {

            //mock dependencies
            StateModel mockStateModel = mock(StateModel.class);
            TranslationModel mockTranslationModel = mock(TranslationModel.class);
            LoggerModel mockLoggerModel = mock(LoggerModel.class);

            //configuro mock
            BooleanProperty changesPendingProperty = new SimpleBooleanProperty(false);
            when(mockStateModel.areChangesPending()).thenReturn(changesPendingProperty);
            when(mockTranslationModel.getUiBundle()).thenReturn(mock(ResourceBundle.class));

            stateModelMock.when(StateModel::getInstance).thenReturn(mockStateModel);
            translationModelMock.when(TranslationModel::getInstance).thenReturn(mockTranslationModel);
            loggerModelMock.when(LoggerModel::getInstance).thenReturn(mockLoggerModel);

            //forzo prima inizializzazione con myself a null
            Field myselfField = ConfirmationController.class.getDeclaredField("myself");
            myselfField.setAccessible(true);
            myselfField.set(null, null);

            //prima chiamata
            ConfirmationController firstInstance = ConfirmationController.getInstance();

            ConfirmationController secondInstance = ConfirmationController.getInstance();
            assertEquals(firstInstance, secondInstance);
        }
    }

}