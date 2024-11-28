package org.supsi.view.info;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.supsi.controller.errors.ErrorController;
import org.supsi.model.errors.ErrorModel;
import org.supsi.view.AbstractGUITest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

public class ErrorImageMissingTest extends AbstractGUITest {

    private Path originalImagePath;
    private Path temporaryImagePath;
    @Override
    public void start(Stage stage) throws Exception {
        mockedFileChooser = mockConstruction(FileChooser.class,
                (mock, context) -> {
                    File testFile = new File(getClass().getResource("/image.png").getFile());


                    when(mock.showOpenDialog(any())).thenReturn(testFile);
                    when(mock.showSaveDialog(any())).thenReturn(testFile);
                });

        originalImagePath = Path.of(getClass().getResource("/images/icons/sadFile.png").toURI());
        temporaryImagePath = Path.of(originalImagePath.toString() + ".backup");

        // Move the image to a temporary location
        Files.move(originalImagePath, temporaryImagePath, StandardCopyOption.REPLACE_EXISTING);
        super.start(stage);
    }

    @AfterEach
    public void cleanup() throws IOException {
        // Restore the image
        if (Files.exists(temporaryImagePath)) {
            Files.move(temporaryImagePath, originalImagePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    @Test
    public void walkThrough() {
        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#root");
        openImage();
        verifyPopUp();
        closePopup();
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

    private void verifyPopUp() {
        step("verify popup component", () -> {
            sleep(SLEEP_INTERVAL);
            verifyThat("#errorCloseBtn", isVisible());
            verifyThat("#errorMessageHeader", isVisible());
            verifyThat("#errorMessage", isVisible());
        });
    }

    private void closePopup() {
        step("close popup", () -> {
            sleep(SLEEP_INTERVAL);
            clickOn("#errorCloseBtn");
            sleep(SLEEP_INTERVAL);
        });
    }
}