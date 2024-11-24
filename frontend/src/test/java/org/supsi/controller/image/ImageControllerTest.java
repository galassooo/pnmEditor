package org.supsi.controller.image;

import ch.supsi.application.image.ImageApplication;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.supsi.controller.confirmation.ConfirmationController;
import org.supsi.controller.errors.ErrorController;
import org.supsi.model.event.EventManager;
import org.supsi.model.image.ImageModel;
import org.supsi.model.info.LoggerModel;
import org.supsi.model.state.StateModel;
import org.supsi.model.translations.TranslationModel;
import org.supsi.view.IView;
import org.supsi.view.fileSystem.FileSystemView;
import org.supsi.view.fileSystem.IFileSystemView;
import org.supsi.view.image.ExportEvent;
import org.supsi.view.image.IImageView;
import org.supsi.view.info.ConfirmPopup;
import org.testfx.framework.junit5.Start;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ImageControllerTest {

    @Mock
    private ImageModel mockImageModel;

    @Mock
    private ErrorController mockErrorController;

    @Mock
    private LoggerModel mockLoggerModel;

    @Mock
    private ConfirmationController mockConfirmationController;

    @Mock
    private StateModel mockStateModel;

    @Mock
    private EventManager mockEventManager;

    @Mock
    private IImageView mockImageView;

    @Mock
    private FileSystemView mockFileSystemView;

    @Mock
    private File mockFile;

    @Captor
    private ArgumentCaptor<Consumer<?>> consumerCaptor;

    private ImageController imageController;


    @Mock
    private ResourceBundle mockBundle;


    @BeforeEach
    void setUp() throws Exception {
        var field = ImageController.class.getDeclaredField("myself");
        field.setAccessible(true);
        field.set(null, null);

    }

    @Test
    void testGetInstance() {

        BooleanProperty mockRefreshRequiredProperty = new SimpleBooleanProperty(false);
        when(mockStateModel.refreshRequiredProperty()).thenReturn(mockRefreshRequiredProperty);

        try (MockedStatic<ImageModel> imageModelMockedStatic = mockStatic(ImageModel.class);
             MockedStatic<ErrorController> errorControllerMockedStatic = mockStatic(ErrorController.class);
             MockedStatic<LoggerModel> loggerModelMockedStatic = mockStatic(LoggerModel.class);
             MockedStatic<ConfirmationController> confControllerMockedStatic = mockStatic(ConfirmationController.class);
             MockedStatic<StateModel> stateModelMockedStatic = mockStatic(StateModel.class);
             MockedStatic<EventManager> eventManagerMockedStatic = mockStatic(EventManager.class)) {

            imageModelMockedStatic.when(ImageModel::getInstance).thenReturn(mockImageModel);
            errorControllerMockedStatic.when(ErrorController::getInstance).thenReturn(mockErrorController);
            loggerModelMockedStatic.when(LoggerModel::getInstance).thenReturn(mockLoggerModel);
            confControllerMockedStatic.when(ConfirmationController::getInstance).thenReturn(mockConfirmationController);
            stateModelMockedStatic.when(StateModel::getInstance).thenReturn(mockStateModel);
            eventManagerMockedStatic.when(EventManager::getSubscriber).thenReturn(mockEventManager);

            ImageController controller1 = ImageController.getInstance();
            ImageController controller2 = ImageController.getInstance();

            assertEquals(controller1, controller2);
        }
    }

    private void setupMocks() {

        BooleanProperty mockRefreshRequiredProperty = new SimpleBooleanProperty(false);
        when(mockStateModel.refreshRequiredProperty()).thenReturn(mockRefreshRequiredProperty);

        try (MockedStatic<ImageModel> imageModelMockedStatic = mockStatic(ImageModel.class);
             MockedStatic<ErrorController> errorControllerMockedStatic = mockStatic(ErrorController.class);
             MockedStatic<LoggerModel> loggerModelMockedStatic = mockStatic(LoggerModel.class);
             MockedStatic<ConfirmationController> confControllerMockedStatic = mockStatic(ConfirmationController.class);
             MockedStatic<StateModel> stateModelMockedStatic = mockStatic(StateModel.class);
             MockedStatic<EventManager> eventManagerMockedStatic = mockStatic(EventManager.class)) {

            imageModelMockedStatic.when(ImageModel::getInstance).thenReturn(mockImageModel);
            errorControllerMockedStatic.when(ErrorController::getInstance).thenReturn(mockErrorController);
            loggerModelMockedStatic.when(LoggerModel::getInstance).thenReturn(mockLoggerModel);
            confControllerMockedStatic.when(ConfirmationController::getInstance).thenReturn(mockConfirmationController);
            stateModelMockedStatic.when(StateModel::getInstance).thenReturn(mockStateModel);
            eventManagerMockedStatic.when(EventManager::getSubscriber).thenReturn(mockEventManager);

            imageController = new ImageController() {
                @Override
                protected FileSystemView getFileSystemView(Stage stage) {
                    return mockFileSystemView;
                }
            };
        }
    }

    @Test
    void testSaveAs() throws Exception {

        BooleanProperty mockRefreshRequiredProperty = new SimpleBooleanProperty(false);
        when(mockStateModel.refreshRequiredProperty()).thenReturn(mockRefreshRequiredProperty);

        setupMocks();
        String testPath = "/test/path/image.jpg";
        when(mockFile.getPath()).thenReturn(testPath);
        when(mockFileSystemView.askForDirectory()).thenReturn(mockFile);

        imageController.saveAs();

        verify(mockImageModel).writeImage(testPath);
        verify(mockLoggerModel).addInfo("ui_image_saved");
        verifyNoInteractions(mockErrorController);
    }

    @Test
    void testSaveAsUserCancel() {

        BooleanProperty mockRefreshRequiredProperty = new SimpleBooleanProperty(false);
        when(mockStateModel.refreshRequiredProperty()).thenReturn(mockRefreshRequiredProperty);


        setupMocks();
        when(mockFileSystemView.askForDirectory()).thenReturn(null);

        imageController.saveAs();

        verifyNoInteractions(mockImageModel);
        verifyNoInteractions(mockErrorController);
        verifyNoInteractions(mockLoggerModel);
    }

    @Test
    void testSaveAsEx() throws Exception {

        BooleanProperty mockRefreshRequiredProperty = new SimpleBooleanProperty(false);
        when(mockStateModel.refreshRequiredProperty()).thenReturn(mockRefreshRequiredProperty);

        setupMocks();
        String errorMessage = "Failed to write image";
        String testPath = "/test/path/image.jpg";
        when(mockFile.getPath()).thenReturn(testPath);
        when(mockFileSystemView.askForDirectory()).thenReturn(mockFile);
        doThrow(new IOException(errorMessage)).when(mockImageModel).writeImage(testPath);

        imageController.saveAs();

        verify(mockErrorController).showError(errorMessage);
        verify(mockLoggerModel).addInfo("ui_image_saved");
    }


    @Test
    void testSave() throws IOException, IllegalAccessException {
        BooleanProperty mockRefreshRequiredProperty = new SimpleBooleanProperty(false);
        when(mockStateModel.refreshRequiredProperty()).thenReturn(mockRefreshRequiredProperty);

        try (MockedStatic<ImageModel> imageModelMockedStatic = mockStatic(ImageModel.class);
             MockedStatic<ErrorController> errorControllerMockedStatic = mockStatic(ErrorController.class);
             MockedStatic<LoggerModel> loggerModelMockedStatic = mockStatic(LoggerModel.class);
             MockedStatic<ConfirmationController> confControllerMockedStatic = mockStatic(ConfirmationController.class);
             MockedStatic<StateModel> stateModelMockedStatic = mockStatic(StateModel.class);
             MockedStatic<EventManager> eventManagerMockedStatic = mockStatic(EventManager.class)) {

            imageModelMockedStatic.when(ImageModel::getInstance).thenReturn(mockImageModel);
            errorControllerMockedStatic.when(ErrorController::getInstance).thenReturn(mockErrorController);
            loggerModelMockedStatic.when(LoggerModel::getInstance).thenReturn(mockLoggerModel);
            confControllerMockedStatic.when(ConfirmationController::getInstance).thenReturn(mockConfirmationController);
            stateModelMockedStatic.when(StateModel::getInstance).thenReturn(mockStateModel);
            eventManagerMockedStatic.when(EventManager::getSubscriber).thenReturn(mockEventManager);

            imageController = ImageController.getInstance();

            imageController.save();

            verify(mockImageModel).writeImage(null);

        }
    }

    @Test
    void testSaveEx() throws IOException, IllegalAccessException {
        BooleanProperty mockRefreshRequiredProperty = new SimpleBooleanProperty(false);
        when(mockStateModel.refreshRequiredProperty()).thenReturn(mockRefreshRequiredProperty);

        try (MockedStatic<ImageModel> imageModelMockedStatic = mockStatic(ImageModel.class);
             MockedStatic<ErrorController> errorControllerMockedStatic = mockStatic(ErrorController.class);
             MockedStatic<LoggerModel> loggerModelMockedStatic = mockStatic(LoggerModel.class);
             MockedStatic<ConfirmationController> confControllerMockedStatic = mockStatic(ConfirmationController.class);
             MockedStatic<StateModel> stateModelMockedStatic = mockStatic(StateModel.class);
             MockedStatic<EventManager> eventManagerMockedStatic = mockStatic(EventManager.class)) {

            imageModelMockedStatic.when(ImageModel::getInstance).thenReturn(mockImageModel);
            errorControllerMockedStatic.when(ErrorController::getInstance).thenReturn(mockErrorController);
            loggerModelMockedStatic.when(LoggerModel::getInstance).thenReturn(mockLoggerModel);
            confControllerMockedStatic.when(ConfirmationController::getInstance).thenReturn(mockConfirmationController);
            stateModelMockedStatic.when(StateModel::getInstance).thenReturn(mockStateModel);
            eventManagerMockedStatic.when(EventManager::getSubscriber).thenReturn(mockEventManager);

            imageController = ImageController.getInstance();


            doThrow(IOException.class).when(mockImageModel).writeImage(null);
            imageController.save();

            verify(mockImageModel).writeImage(null);

        }
    }

    @Test
    void testSetStage() throws IOException, IllegalAccessException {
        BooleanProperty mockRefreshRequiredProperty = new SimpleBooleanProperty(false);
        when(mockStateModel.refreshRequiredProperty()).thenReturn(mockRefreshRequiredProperty);

        setupMocks();
        Stage mockStage = mock(Stage.class);
        imageController.setStage(mockStage);

        // Test che il stage è stato impostato verificando che viene usato in altri metodi
        String testPath = "/test/path/image.jpg";
        when(mockFile.getPath()).thenReturn(testPath);
        when(mockFileSystemView.askForDirectory()).thenReturn(mockFile);

        imageController.saveAs();
        verify(mockImageModel).writeImage(testPath);
    }

    @Test
    void testOnExportRequested() throws IOException, IllegalAccessException {
        BooleanProperty mockRefreshRequiredProperty = new SimpleBooleanProperty(false);
        when(mockStateModel.refreshRequiredProperty()).thenReturn(mockRefreshRequiredProperty);

        setupMocks();
        String testPath = "/test/path/image.jpg";
        String extension = "png";
        when(mockFile.getPath()).thenReturn(testPath);
        when(mockFileSystemView.askForDirectory()).thenReturn(mockFile);

        ExportEvent.ExportRequested event = new ExportEvent.ExportRequested(extension);

        // Invoke onExportRequested through reflection since it's private
        try {
            Method method = ImageController.class.getDeclaredMethod("onExportRequested",
                    ExportEvent.ExportRequested.class);
            method.setAccessible(true);
            method.invoke(imageController, event);

            verify(mockFileSystemView).setFileExtension(extension);
            verify(mockImageModel).export(extension, testPath);
            verify(mockLoggerModel).addInfo("ui_image_exported");
        } catch (Exception e) {
            fail("Failed to invoke onExportRequested: " + e.getMessage());
        }
    }

    @Test
    void testOnExportRequestedWhenUserCancels() {
        BooleanProperty mockRefreshRequiredProperty = new SimpleBooleanProperty(false);
        when(mockStateModel.refreshRequiredProperty()).thenReturn(mockRefreshRequiredProperty);

        setupMocks();
        when(mockFileSystemView.askForDirectory()).thenReturn(null);
        String extension = "png";

        ExportEvent.ExportRequested event = new ExportEvent.ExportRequested(extension);

        try {
            Method method = ImageController.class.getDeclaredMethod("onExportRequested",
                    ExportEvent.ExportRequested.class);
            method.setAccessible(true);
            method.invoke(imageController, event);

            verify(mockFileSystemView).setFileExtension(extension);
            verifyNoInteractions(mockImageModel);
            verifyNoInteractions(mockLoggerModel);
        } catch (Exception e) {
            fail("Failed to invoke onExportRequested: " + e.getMessage());
        }
    }

    @Test
    void testOnExportRequestedWithError() throws IOException, IllegalAccessException {
        BooleanProperty mockRefreshRequiredProperty = new SimpleBooleanProperty(false);
        when(mockStateModel.refreshRequiredProperty()).thenReturn(mockRefreshRequiredProperty);

        setupMocks();
        String testPath = "/test/path/image.jpg";
        String extension = "png";
        String errorMessage = "Export failed";
        when(mockFile.getPath()).thenReturn(testPath);
        when(mockFileSystemView.askForDirectory()).thenReturn(mockFile);
        doThrow(new IOException(errorMessage)).when(mockImageModel).export(extension, testPath);

        ExportEvent.ExportRequested event = new ExportEvent.ExportRequested(extension);

        try {
            Method method = ImageController.class.getDeclaredMethod("onExportRequested",
                    ExportEvent.ExportRequested.class);
            method.setAccessible(true);
            method.invoke(imageController, event);

            verify(mockErrorController).showError(errorMessage);
            verify(mockLoggerModel).addInfo("ui_image_exported");
        } catch (Exception e) {
            fail("Failed to invoke onExportRequested: " + e.getMessage());
        }
    }

    @Test
    void testGetFileSystemView() {
        BooleanProperty mockRefreshRequiredProperty = new SimpleBooleanProperty(false);
        when(mockStateModel.refreshRequiredProperty()).thenReturn(mockRefreshRequiredProperty);
        try (MockedStatic<ImageModel> imageModelMockedStatic = mockStatic(ImageModel.class);
             MockedStatic<ErrorController> errorControllerMockedStatic = mockStatic(ErrorController.class);
             MockedStatic<LoggerModel> loggerModelMockedStatic = mockStatic(LoggerModel.class);
             MockedStatic<ConfirmationController> confControllerMockedStatic = mockStatic(ConfirmationController.class);
             MockedStatic<StateModel> stateModelMockedStatic = mockStatic(StateModel.class);
             MockedStatic<EventManager> eventManagerMockedStatic = mockStatic(EventManager.class)) {

            imageModelMockedStatic.when(ImageModel::getInstance).thenReturn(mockImageModel);
            errorControllerMockedStatic.when(ErrorController::getInstance).thenReturn(mockErrorController);
            loggerModelMockedStatic.when(LoggerModel::getInstance).thenReturn(mockLoggerModel);
            confControllerMockedStatic.when(ConfirmationController::getInstance).thenReturn(mockConfirmationController);
            stateModelMockedStatic.when(StateModel::getInstance).thenReturn(mockStateModel);
            eventManagerMockedStatic.when(EventManager::getSubscriber).thenReturn(mockEventManager);

            ImageController controller = ImageController.getInstance();

            assertNotNull(controller.getFileSystemView(mock(Stage.class)));
        }
    }

    @Test
    void testSetImage() {
        BooleanProperty mockRefreshRequiredProperty = new SimpleBooleanProperty(false);
        when(mockStateModel.refreshRequiredProperty()).thenReturn(mockRefreshRequiredProperty);

        try (MockedStatic<ImageModel> imageModelMockedStatic = mockStatic(ImageModel.class);
             MockedStatic<ErrorController> errorControllerMockedStatic = mockStatic(ErrorController.class);
             MockedStatic<LoggerModel> loggerModelMockedStatic = mockStatic(LoggerModel.class);
             MockedStatic<ConfirmationController> confControllerMockedStatic = mockStatic(ConfirmationController.class);
             MockedStatic<StateModel> stateModelMockedStatic = mockStatic(StateModel.class);
             MockedStatic<EventManager> eventManagerMockedStatic = mockStatic(EventManager.class)) {

            imageModelMockedStatic.when(ImageModel::getInstance).thenReturn(mockImageModel);
            errorControllerMockedStatic.when(ErrorController::getInstance).thenReturn(mockErrorController);
            loggerModelMockedStatic.when(LoggerModel::getInstance).thenReturn(mockLoggerModel);
            confControllerMockedStatic.when(ConfirmationController::getInstance).thenReturn(mockConfirmationController);
            stateModelMockedStatic.when(StateModel::getInstance).thenReturn(mockStateModel);
            eventManagerMockedStatic.when(EventManager::getSubscriber).thenReturn(mockEventManager);

            imageController = ImageController.getInstance();
            //non so come testare un setter senza getter...
            assertDoesNotThrow(()->imageController.setImage(mockImageView));
        }
    }

    @Test
    void testOpen() throws Exception {
        BooleanProperty mockRefreshRequiredProperty = new SimpleBooleanProperty(false);
        when(mockStateModel.refreshRequiredProperty()).thenReturn(mockRefreshRequiredProperty);

        setupMocks();
        var confField = ImageController.class.getDeclaredField("confirmationController");
        confField.setAccessible(true);
        confField.set(imageController, setUpConfirmCtrl());

        when(mockFileSystemView.askForFile()).thenReturn(mockFile);
        Stage mockStage = mock(Stage.class);

        imageController.setStage(mockStage);

        imageController.open();
        verify(mockImageModel).readImage(null);
        verify(mockLoggerModel).addInfo("ui_image_loaded");
    }

    ConfirmationController setUpConfirmCtrl() throws Exception {

        // Reset singleton
        Field instanceField = ConfirmationController.class.getDeclaredField("myself");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
        SimpleBooleanProperty changesPendingProperty;
        changesPendingProperty = new SimpleBooleanProperty(false);

        ConfirmPopup mockView = mock(ConfirmPopup.class);
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

            return ConfirmationController.getInstance();

        }
    }

    @Test
    void testOpenEx() throws Exception {
        BooleanProperty mockRefreshRequiredProperty = new SimpleBooleanProperty(false);
        when(mockStateModel.refreshRequiredProperty()).thenReturn(mockRefreshRequiredProperty);

        setupMocks();
        //obbligata a farlo
        var confField = ImageController.class.getDeclaredField("confirmationController");
        confField.setAccessible(true);
        confField.set(imageController, setUpConfirmCtrl());

        when(mockFileSystemView.askForFile()).thenReturn(mockFile);
        Stage mockStage = mock(Stage.class);

        doThrow(IOException.class).when(mockImageModel).readImage(anyString());
        doNothing().when(mockErrorController).showError(anyString());
        imageController.setStage(mockStage);

        imageController.open();

        verify(mockImageModel).readImage(null);
        verify(mockErrorController).showError(anyString());
    }

    @Test
    void testOpenChosenNull() throws Exception {
        BooleanProperty mockRefreshRequiredProperty = new SimpleBooleanProperty(false);
        when(mockStateModel.refreshRequiredProperty()).thenReturn(mockRefreshRequiredProperty);

        setupMocks();
        //obbligata a farlo
        var confField = ImageController.class.getDeclaredField("confirmationController");
        confField.setAccessible(true);
        confField.set(imageController, setUpConfirmCtrl());

        when(mockFileSystemView.askForFile()).thenReturn(null);
        Stage mockStage = mock(Stage.class);

        imageController.setStage(mockStage);

        imageController.open();
        verifyNoInteractions(mockImageModel);
        verifyNoInteractions(mockLoggerModel);
    }

    @Test
    void testBinding() throws Exception {
        try (MockedStatic<ImageModel> imageModelMockedStatic = mockStatic(ImageModel.class);
             MockedStatic<ErrorController> errorControllerMockedStatic = mockStatic(ErrorController.class);
             MockedStatic<LoggerModel> loggerModelMockedStatic = mockStatic(LoggerModel.class);
             MockedStatic<ConfirmationController> confControllerMockedStatic = mockStatic(ConfirmationController.class);
             MockedStatic<StateModel> stateModelMockedStatic = mockStatic(StateModel.class);
             MockedStatic<EventManager> eventManagerMockedStatic = mockStatic(EventManager.class)) {

            imageModelMockedStatic.when(ImageModel::getInstance).thenReturn(mockImageModel);
            errorControllerMockedStatic.when(ErrorController::getInstance).thenReturn(mockErrorController);
            loggerModelMockedStatic.when(LoggerModel::getInstance).thenReturn(mockLoggerModel);
            confControllerMockedStatic.when(ConfirmationController::getInstance).thenReturn(mockConfirmationController);
            stateModelMockedStatic.when(StateModel::getInstance).thenReturn(mockStateModel);
            eventManagerMockedStatic.when(EventManager::getSubscriber).thenReturn(mockEventManager);

            BooleanProperty mockRefreshRequiredProperty = new SimpleBooleanProperty(false);
            when(mockStateModel.refreshRequiredProperty()).thenReturn(mockRefreshRequiredProperty);


            ImageController controller1 = ImageController.getInstance();
            controller1.setImage(mockImageView);
            verify(mockStateModel).refreshRequiredProperty();
            //trigger the property change
            mockRefreshRequiredProperty.set(true);
            verify(mockImageView).update();
            mockRefreshRequiredProperty.set(false);
            mockRefreshRequiredProperty.set(true);
            verify(mockImageView, times(2)).update();

        }
    }
}