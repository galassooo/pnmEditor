package org.supsi.controller.preferences;

import javafx.fxml.FXMLLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.supsi.controller.errors.ErrorController;
import org.supsi.model.errors.ErrorModel;
import org.supsi.model.event.EventManager;
import org.supsi.model.event.EventSubscriber;
import org.supsi.model.info.LoggerModel;
import org.supsi.model.preferences.IPreferencesModel;
import org.supsi.model.preferences.PreferencesModel;
import org.supsi.model.translations.TranslationModel;
import org.supsi.view.IView;
import org.supsi.view.preferences.PreferenceEvent;
import org.supsi.view.preferences.PreferencesView;

import java.io.IOException;
import java.net.URL;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.Preferences;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PreferencesControllerTest {

    @Mock
    private PreferencesModel mockPreferencesModel;

    @Mock
    private LoggerModel mockLoggerModel;

    @Mock
    private EventSubscriber mockEventSubscriber;

    @Mock
    private PreferencesView mockView;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        // Reset singleton
        var field = PreferencesController.class.getDeclaredField("mySelf");
        field.setAccessible(true);
        field.set(null, null);
    }

    @Test
    void testSingleton() {
        try (MockedStatic<PreferencesModel> preferencesModelMock = mockStatic(PreferencesModel.class);
             MockedStatic<LoggerModel> loggerModelMock = mockStatic(LoggerModel.class);
             MockedStatic<EventManager> eventManagerMock = mockStatic(EventManager.class);
             MockedConstruction<FXMLLoader> fxmlLoaderMock = mockConstruction(FXMLLoader.class,
                     (mockLoader, context) -> {
                         when(mockLoader.load()).thenReturn(null);
                         when(mockLoader.getController()).thenReturn(mockView);
                     })) {

            preferencesModelMock.when(PreferencesModel::getInstance).thenReturn(mockPreferencesModel);
            loggerModelMock.when(LoggerModel::getInstance).thenReturn(mockLoggerModel);
            eventManagerMock.when(EventManager::getSubscriber).thenReturn(mockEventSubscriber);

            PreferencesController controller1 = PreferencesController.getInstance();
            PreferencesController controller2 = PreferencesController.getInstance();

            assertEquals(controller1, controller2); // Singleton test
        }
    }

    @Test
    void testBuildNullResource() {
        try (MockedStatic<PreferencesModel> preferencesModelMock = mockStatic(PreferencesModel.class);
             MockedStatic<LoggerModel> loggerModelMock = mockStatic(LoggerModel.class);
             MockedStatic<EventManager> eventManagerMock = mockStatic(EventManager.class);
             MockedConstruction<FXMLLoader> fxmlLoaderMock = mockConstruction(FXMLLoader.class,
                (mockLoader, context) -> {
                    when(mockLoader.load()).thenReturn(null);
                    when(mockLoader.getController()).thenReturn(mockView);
                })) {

            preferencesModelMock.when(PreferencesModel::getInstance).thenReturn(mockPreferencesModel);
            loggerModelMock.when(LoggerModel::getInstance).thenReturn(mockLoggerModel);
            eventManagerMock.when(EventManager::getSubscriber).thenReturn(mockEventSubscriber);

            PreferencesController controller1 = PreferencesController.getInstance();
            PreferencesController controller2 = PreferencesController.getInstance();

            assertEquals(controller1, controller2); // Singleton test
        }
    }

    @Test
    void testFXMLLoadNullURL(){
        try (MockedStatic<PreferencesModel> preferencesModelMock = mockStatic(PreferencesModel.class);
             MockedStatic<LoggerModel> loggerModelMock = mockStatic(LoggerModel.class);
             MockedStatic<EventManager> eventManagerMock = mockStatic(EventManager.class);
             MockedConstruction<FXMLLoader> fxmlLoaderMock = mockConstruction(FXMLLoader.class,
                     (mockLoader, context) -> {
                         when(mockLoader.load()).thenReturn(null);
                         when(mockLoader.getController()).thenReturn(mockView);
                     })) {


            preferencesModelMock.when(PreferencesModel::getInstance).thenReturn(mockPreferencesModel);
            loggerModelMock.when(LoggerModel::getInstance).thenReturn(mockLoggerModel);
            eventManagerMock.when(EventManager::getSubscriber).thenReturn(mockEventSubscriber);

            assertDoesNotThrow(()-> spy(new PreferencesController() {
                @Override
                public URL getResource(String name) {
                    return null;
                }
            }));
        }
    }

    @Test
    void testShowPreferencesPopup() throws Exception {
        try (MockedStatic<PreferencesModel> preferencesModelMock = mockStatic(PreferencesModel.class);
             MockedStatic<LoggerModel> loggerModelMock = mockStatic(LoggerModel.class);
             MockedConstruction<FXMLLoader> fxmlLoaderMock = mockConstruction(FXMLLoader.class,
                     (mockLoader, context) -> {
                         when(mockLoader.load()).thenReturn(null);
                         when(mockLoader.getController()).thenReturn(mockView);
                     })) {

            preferencesModelMock.when(PreferencesModel::getInstance).thenReturn(mockPreferencesModel);
            loggerModelMock.when(LoggerModel::getInstance).thenReturn(mockLoggerModel);

            PreferencesController controller = PreferencesController.getInstance();

            controller.showPreferencesPopup();

            verify(mockLoggerModel).addDebug("ui_start_popup_build");
            verify(mockLoggerModel).addDebug("ui_end_popup_build");
            verify(mockLoggerModel).addDebug("ui_popup_show");
            verify(mockView).build();
            verify(mockView).show();
        }
    }


    @Test
    void testFXMLLoadFailure() {
        try (MockedStatic<PreferencesModel> preferencesModelMock = mockStatic(PreferencesModel.class);
             MockedStatic<LoggerModel> loggerModelMock = mockStatic(LoggerModel.class);
             MockedConstruction<FXMLLoader> fxmlLoaderMock = mockConstruction(FXMLLoader.class,
                     (mockLoader, context) -> {
                         when(mockLoader.load()).thenThrow(new IOException("Test IOException"));
                     })) {

            preferencesModelMock.when(PreferencesModel::getInstance).thenReturn(mockPreferencesModel);
            loggerModelMock.when(LoggerModel::getInstance).thenReturn(mockLoggerModel);

            PreferencesController controller = PreferencesController.getInstance();

            verify(mockLoggerModel, never()).addError("ui_failed_to_load_component");
        }
    }
    @Test
    void testPreferenceChangeSuccess() throws Exception {
        try (MockedStatic<PreferencesModel> preferencesModelMock = mockStatic(PreferencesModel.class);
             MockedStatic<LoggerModel> loggerModelMock = mockStatic(LoggerModel.class);
             MockedStatic<EventManager> eventManagerMock = mockStatic(EventManager.class);
             MockedConstruction<FXMLLoader> fxmlLoaderMock = mockConstruction(FXMLLoader.class,
                     (mockLoader, context) -> {
                         when(mockLoader.load()).thenReturn(null);
                         when(mockLoader.getController()).thenReturn(mockView);
                     })) {

            preferencesModelMock.when(PreferencesModel::getInstance).thenReturn(mockPreferencesModel);
            loggerModelMock.when(LoggerModel::getInstance).thenReturn(mockLoggerModel);
            eventManagerMock.when(EventManager::getSubscriber).thenReturn(mockEventSubscriber);

            PreferencesController controller = PreferencesController.getInstance();

            Preferences dummyPreferences = Preferences.userRoot().node("dummy");
            PreferenceChangeEvent pEvent = new PreferenceChangeEvent(dummyPreferences, "language-tag", "en-US");

            // creo evenyo
            PreferenceEvent.PreferenceChanged event = new PreferenceEvent.PreferenceChanged(pEvent);

            controller.preferenceChange(event);


            verify(mockPreferencesModel).setPreference("language-tag", "en-US");
            verify(mockLoggerModel).addInfo("ui_preferences_stored");
        }
    }

    @Test
    void testPreferenceChangeWithIOException() throws Exception {
        try (MockedStatic<PreferencesModel> preferencesModelMock = mockStatic(PreferencesModel.class);
             MockedStatic<LoggerModel> loggerModelMock = mockStatic(LoggerModel.class);
             MockedStatic<ErrorController> errorControllerMock = mockStatic(ErrorController.class);
             MockedStatic<EventManager> eventManagerMock = mockStatic(EventManager.class);
             MockedConstruction<FXMLLoader> fxmlLoaderMock = mockConstruction(FXMLLoader.class,
                     (mockLoader, context) -> {
                         when(mockLoader.load()).thenReturn(null);
                         when(mockLoader.getController()).thenReturn(mockView);
                     })) {

            preferencesModelMock.when(PreferencesModel::getInstance).thenReturn(mockPreferencesModel);
            loggerModelMock.when(LoggerModel::getInstance).thenReturn(mockLoggerModel);
            eventManagerMock.when(EventManager::getSubscriber).thenReturn(mockEventSubscriber);

            ErrorController mockErrorController = mock(ErrorController.class);
            errorControllerMock.when(ErrorController::getInstance).thenReturn(mockErrorController);
            //simulate ex
            doThrow(new IOException("Test IOException")).when(mockPreferencesModel).setPreference(anyString(), anyString());

            PreferencesController controller = PreferencesController.getInstance();

            Preferences dummyPreferences = Preferences.userRoot().node("dummy");
            PreferenceChangeEvent pEvent = new PreferenceChangeEvent(dummyPreferences, "language-tag", "en-US");

            PreferenceEvent.PreferenceChanged event = new PreferenceEvent.PreferenceChanged(pEvent);

            controller.preferenceChange(event);

            verify(mockErrorController).showError("Test IOException");
        }
    }
}
