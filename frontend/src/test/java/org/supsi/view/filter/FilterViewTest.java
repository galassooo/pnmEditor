package org.supsi.view.filter;

import com.sun.javafx.scene.control.ContextMenuContent;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.supsi.MainFx;
import org.supsi.view.AbstractGUITest;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

public class FilterViewTest extends AbstractGUITest {


    @Test
    void test(){
        clickOn("#root");
        openImage();
        testFilterMenu();
        testFilterLine();
        testFilterAdd();
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

    private void testFilterMenu(){
        step("filter menu", () -> {
            sleep(SLEEP_INTERVAL);
            clickOn("#filterMenu");
            sleep(SLEEP_INTERVAL);

            verifyThat("#Negative", isVisible());
            verifyThat("#Rotate_Right", isVisible());
            verifyThat("#Rotate_Left", isVisible());
            verifyThat("#Mirror_Horizontal", isVisible());
            verifyThat("#Mirror_Vertical", isVisible());

            MenuItem negative = lookup("#Negative").queryAs(ContextMenuContent.MenuItemContainer.class).getItem();
            MenuItem rotateRight = lookup("#Rotate_Right").queryAs(ContextMenuContent.MenuItemContainer.class).getItem();
            MenuItem rotateLeft = lookup("#Rotate_Left").queryAs(ContextMenuContent.MenuItemContainer.class).getItem();
            MenuItem mirrorH = lookup("#Mirror_Horizontal").queryAs(ContextMenuContent.MenuItemContainer.class).getItem();
            MenuItem mirrorV = lookup("#Mirror_Vertical").queryAs(ContextMenuContent.MenuItemContainer.class).getItem();

            assertFalse(negative.isDisable());
            assertFalse(rotateRight.isDisable());
            assertFalse(rotateLeft.isDisable());
            assertFalse(mirrorH.isDisable());
            assertFalse(mirrorV.isDisable());

            clickOn("#root");
        });

    }

    private void testFilterLine(){
        step("filter line", () -> {
            sleep(SLEEP_INTERVAL);
            clickOn("#root");
            sleep(SLEEP_INTERVAL);

            verifyThat("#Negative_line", isVisible());
            verifyThat("#Rotate_Right_line", isVisible());
            verifyThat("#Rotate_Left_line", isVisible());
            verifyThat("#Mirror_Horizontal_line", isVisible());
            verifyThat("#Mirror_Vertical_line", isVisible());

            Button negative = lookup("#Negative_line").queryAs(Button.class);
            Button rotateRight = lookup("#Rotate_Right_line").queryAs(Button.class);
            Button rotateLeft = lookup("#Rotate_Left_line").queryAs(Button.class);
            Button mirrorH = lookup("#Mirror_Horizontal_line").queryAs(Button.class);
            Button mirrorV = lookup("#Mirror_Vertical_line").queryAs(Button.class);

            assertFalse(negative.isDisable());
            assertFalse(rotateRight.isDisable());
            assertFalse(rotateLeft.isDisable());
            assertFalse(mirrorH.isDisable());
            assertFalse(mirrorV.isDisable());
        });
    }

    private void testFilterAdd(){
        step("filter add", () -> {
            sleep(SLEEP_INTERVAL);
            clickOn("#root");
            sleep(SLEEP_INTERVAL);

            clickOn("#Negative_line");
            sleep(SLEEP_INTERVAL);
            clickOn("#Rotate_Right_line");
            sleep(SLEEP_INTERVAL);
            clickOn("#Rotate_Left_line");
            sleep(SLEEP_INTERVAL);
            clickOn("#Mirror_Horizontal_line");
            sleep(SLEEP_INTERVAL);
            clickOn("#Mirror_Vertical_line");
        });
    }
}
