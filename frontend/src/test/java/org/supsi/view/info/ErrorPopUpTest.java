package org.supsi.view.info;

import javafx.fxml.FXMLLoader;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.supsi.controller.errors.ErrorController;
import org.supsi.model.info.LoggerModel;
import org.supsi.view.AbstractGUITest;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

public class ErrorPopUpTest extends AbstractGUITest {

    @Override
    public void start(Stage stage) throws Exception {
        mockedFileChooser = mockConstruction(FileChooser.class,
                (mock, context) -> {
                    File testFile = new File(getClass().getResource("/image.png").getFile());


                    when(mock.showOpenDialog(any())).thenReturn(testFile);
                    when(mock.showSaveDialog(any())).thenReturn(testFile);
                });
        super.start(stage);
    }

    @Override
    public void stop(){
        mockedFileChooser.close();
    }

    @Test
    public void walkThrough() {
        clickOn("#root");
        openImage();
        verifyPopUp();
        closePopup();
        openImage();

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

            verifyThat("#errorCloseBtn", isVisible());
            verifyThat("#errorMessageHeader", isVisible());
            verifyThat("#errorMessage", isVisible());
            verifyThat("#errorTitle", isVisible());
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

