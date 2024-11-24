package org.supsi.dispatcher;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.supsi.controller.about.AboutController;
import org.supsi.controller.exit.ExitController;
import org.supsi.controller.image.ImageController;
import org.supsi.controller.preferences.PreferencesController;
import org.supsi.model.state.StateModel;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuDispatcherTest {

    private MenuDispatcher menuDispatcher;

    @Mock
    private ImageController mockImageController;

    @Mock
    private PreferencesController mockPreferencesController;

    @Mock
    private AboutController mockAboutController;

    @Mock
    private ExitController mockExitController;

    @Mock
    private StateModel mockStateModel;

    @Mock
    private Stage mockStage;

    @Mock
    private MenuItem mockSaveMenuItem;

    @Mock
    private MenuItem mockSaveAsMenuItem;

    private SimpleBooleanProperty saveDisableProperty;
    private SimpleBooleanProperty saveAsDisableProperty;
    private SimpleBooleanProperty canSaveProperty;
    private SimpleBooleanProperty canSaveAsProperty;

    @BeforeEach
    void setUp() throws Exception {
        saveDisableProperty = new SimpleBooleanProperty();
        saveAsDisableProperty = new SimpleBooleanProperty();
        canSaveProperty = new SimpleBooleanProperty();
        canSaveAsProperty = new SimpleBooleanProperty();

        when(mockSaveMenuItem.disableProperty()).thenReturn(saveDisableProperty);
        when(mockSaveAsMenuItem.disableProperty()).thenReturn(saveAsDisableProperty);

        try (MockedStatic<ImageController> imageControllerMock = mockStatic(ImageController.class);
             MockedStatic<PreferencesController> preferencesMock = mockStatic(PreferencesController.class);
             MockedStatic<AboutController> aboutMock = mockStatic(AboutController.class);
             MockedStatic<ExitController> exitMock = mockStatic(ExitController.class);
             MockedStatic<StateModel> stateMock = mockStatic(StateModel.class)) {

            imageControllerMock.when(ImageController::getInstance).thenReturn(mockImageController);
            preferencesMock.when(PreferencesController::getInstance).thenReturn(mockPreferencesController);
            aboutMock.when(AboutController::getInstance).thenReturn(mockAboutController);
            exitMock.when(ExitController::getInstance).thenReturn(mockExitController);
            stateMock.when(StateModel::getInstance).thenReturn(mockStateModel);

            when(mockStateModel.canSaveProperty()).thenReturn(canSaveProperty);
            when(mockStateModel.canSaveAsProperty()).thenReturn(canSaveAsProperty);

            menuDispatcher = new MenuDispatcher();

            java.lang.reflect.Field saveMenuItemField = MenuDispatcher.class.getDeclaredField("saveMenuItem");
            java.lang.reflect.Field saveAsMenuItemField = MenuDispatcher.class.getDeclaredField("saveAsMenuItem");

            saveMenuItemField.setAccessible(true);
            saveAsMenuItemField.setAccessible(true);

            saveMenuItemField.set(menuDispatcher, mockSaveMenuItem);
            saveAsMenuItemField.set(menuDispatcher, mockSaveAsMenuItem);

            menuDispatcher.initialize();
        }
    }

    @Test
    void testSetStage() {
        menuDispatcher.setStage(mockStage);
        menuDispatcher.close();
        verify(mockExitController).handleExit(mockStage);
    }

    @Test
    void testSave() {
        menuDispatcher.save();
        verify(mockImageController).save();
    }

    @Test
    void testSaveAs() {
        menuDispatcher.saveAs();
        verify(mockImageController).saveAs();
    }

    @Test
    void testOpen() {
        menuDispatcher.open();
        verify(mockImageController).open();
    }

    @Test
    void testPreferences() {
        menuDispatcher.preferences();
        verify(mockPreferencesController).showPreferencesPopup();
    }

    @Test
    void testAbout() {
        menuDispatcher.about();
        verify(mockAboutController).showPopup();
    }

    @Test
    void testClose() {
        menuDispatcher.setStage(mockStage);
        menuDispatcher.close();
        verify(mockExitController).handleExit(mockStage);
    }

    @Test
    void testBindings() {
        canSaveProperty.set(true);
        assert !saveDisableProperty.get();

        canSaveProperty.set(false);
        assert saveDisableProperty.get();

        canSaveAsProperty.set(true);
        assert !saveAsDisableProperty.get();

        canSaveAsProperty.set(false);
        assert saveAsDisableProperty.get();
    }
}