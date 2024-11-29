package org.supsi.view.info;

import javafx.application.Platform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.supsi.controller.exit.ExitController;
import org.supsi.model.about.AboutModel;
import org.supsi.view.AbstractGUITest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.File;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

public class ConfirmationPopupTest extends AbstractGUITest {

    MockedStatic<Platform> mockedStatic;
    @Override
    public void start(Stage stage) throws Exception {
        mockedFileChooser = mockConstruction(FileChooser.class,
                (mock, context) -> {
                    File testFile = new File(getClass().getResource("/image.ppm").getFile());
                    when(mock.showOpenDialog(any())).thenReturn(testFile);
                    when(mock.showSaveDialog(any())).thenReturn(testFile);
                });

        mockedStatic = mockStatic(Platform.class);

        mockedStatic.when(Platform::exit).thenAnswer(invocation -> {
            return null;
        });

        super.start(stage);
    }

    @Override
    public void stop() throws Exception {
        mockedFileChooser.close();
        mockedStatic.close();
        super.stop();
    }

    @Test
    void walkthrough(){
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#root");
        openImage();
        addFilter();
        quit();
        verifyPopupElements();
        cancelOperation();
    }

    @Test
    void testQuitReal(){
        openImage();
        addFilter();
        quit();
        confirmOperation();
    }

    private void verifyPopupElements() {
        step("verify confirmation popup elements", () -> {

            verifyThat("#confirmTitle", isVisible());
            verifyThat("#cancelButton", isVisible());
            verifyThat("#confirmButton", isVisible());
        });
    }

    private void openImage() {
        step("menu file click", () -> {
            sleep(SLEEP_INTERVAL);
            clickOn("#fileMenu");
            sleep(SLEEP_INTERVAL);
            clickOn("#openMenuItem");
            sleep(SLEEP_INTERVAL);
        });
    }

    private void addFilter(){
        step("add filter", () ->{
            sleep(SLEEP_INTERVAL);
            clickOn("#Negative_line");
            sleep(SLEEP_INTERVAL);
        });
    }

    private void quit(){
        step("quit", () -> {
            sleep(SLEEP_INTERVAL);
            clickOn("#fileMenu");
            sleep(SLEEP_INTERVAL);
            clickOn("#closeMenuItem");
            sleep(SLEEP_INTERVAL);
        });
    }

    private void cancelOperation(){
        step("cancel operation", () -> {
            sleep(SLEEP_INTERVAL);
            clickOn("#cancelButton");
        });
    }

    private void confirmOperation(){
        step("confirm operation", () -> {
            sleep(SLEEP_INTERVAL);
            clickOn("#confirmButton");
        });
    }
}
