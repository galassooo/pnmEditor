package org.supsi.view.image;

import com.sun.javafx.scene.control.ContextMenuContent;
import javafx.application.Platform;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import org.supsi.MainFx;
import org.supsi.view.AbstractGUITest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class ImageViewTest extends AbstractGUITest {

    @Override
    public void start(Stage stage) throws Exception {
        mockedFileChooser = mockConstruction(FileChooser.class,
                (mock, context) -> {
                    File testFile = new File(getClass().getResource("/image1.ppm").getFile());


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
    void walkThrough() {
        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#root");
        clickMenuFile();
        testOpenDialogMock();
        testSave();
        testSaveAs();
        testExport();
    }

    private void testExport() {
        step("test export", () ->{
            sleep(SLEEP_INTERVAL);
            clickOn("#exportMenu");
            sleep(SLEEP_INTERVAL);


            MenuItem item = lookup("#ppm").queryAs(ContextMenuContent.MenuItemContainer.class).getItem();
            assertFalse(item.isDisable());

            MenuItem item1 = lookup("#pgm").queryAs(ContextMenuContent.MenuItemContainer.class).getItem();
            assertFalse(item1.isDisable());

            MenuItem item2 = lookup("#pbm").queryAs(ContextMenuContent.MenuItemContainer.class).getItem();
            assertFalse(item2.isDisable());

            clickOn("#ppm");
            sleep(SLEEP_INTERVAL);

        });
    }

    private void clickMenuFile() {
        step("menu file click", () -> {
            sleep(SLEEP_INTERVAL);
            clickOn("#fileMenu");
            sleep(SLEEP_INTERVAL);

            MenuItem openMenuItem = lookup("#openMenuItem").queryAs(ContextMenuContent.MenuItemContainer.class).getItem();
            MenuItem saveMenuItem = lookup("#saveMenuItem").queryAs(ContextMenuContent.MenuItemContainer.class).getItem();
            MenuItem saveAsMenuItem = lookup("#saveAsMenuItem").queryAs(ContextMenuContent.MenuItemContainer.class).getItem();
            MenuItem closeMenuItem = lookup("#closeMenuItem").queryAs(ContextMenuContent.MenuItemContainer.class).getItem();

            assertFalse(openMenuItem.isDisable());
            assertFalse(closeMenuItem.isDisable());
            assertTrue(saveMenuItem.isDisable());
            assertTrue(saveAsMenuItem.isDisable());
        });
    }

    private void testOpenDialogMock() {
        step("menu open", () -> {

            MenuItem item = lookup("#openMenuItem").queryAs(ContextMenuContent.MenuItemContainer.class).getItem();
            assertFalse(item.isDisable());

            sleep(SLEEP_INTERVAL);
            clickOn("#openMenuItem");
            sleep(SLEEP_INTERVAL);

        });
    }

    private void testSave() {
        step("menu save", () -> {

            sleep(SLEEP_INTERVAL);
            clickOn("#fileMenu");
            sleep(SLEEP_INTERVAL);

            MenuItem save = lookup("#saveMenuItem").queryAs(ContextMenuContent.MenuItemContainer.class).getItem();
            assertFalse(save.isDisable());
            sleep(SLEEP_INTERVAL);
            clickOn("#saveMenuItem");
            sleep(SLEEP_INTERVAL);

        });
    }

    private void testSaveAs() {
        step("menu saveAs", () -> {

            sleep(SLEEP_INTERVAL);
            clickOn("#fileMenu");
            sleep(SLEEP_INTERVAL);

            MenuItem saveAs = lookup("#saveAsMenuItem").queryAs(ContextMenuContent.MenuItemContainer.class).getItem();
            assertFalse(saveAs.isDisable());
            sleep(SLEEP_INTERVAL);
            clickOn("#saveAsMenuItem");
            sleep(SLEEP_INTERVAL);

        });
    }
}
