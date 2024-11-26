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

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class ImageViewTest extends AbstractGUITest {

    @Test
    void test() {
        clickOn("#root");
        clickMenuFile();
        testOpenDialogMock();
        testSave();
        testSaveAs();
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
        step("menu open mocked", () -> {

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
