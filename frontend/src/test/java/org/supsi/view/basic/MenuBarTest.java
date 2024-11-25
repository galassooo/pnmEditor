package org.supsi.view.basic;

import com.sun.javafx.scene.control.ContextMenuContent;
import javafx.scene.control.MenuItem;
import org.junit.jupiter.api.Test;
import org.supsi.view.AbstractGUITest;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;

import static org.testfx.matcher.base.NodeMatchers.isVisible;

public class MenuBarTest extends AbstractGUITest {

    @Test
    public void walkThrough() {
        testMainScene();
    }

    void testMainScene() {
        step("menu bar structure", () -> {
            verifyThat("#file", isVisible());
            verifyThat("#edit", isVisible());
            verifyThat("#help", isVisible());
        });
    }

    @Test
    void testClickOnMenuItem() {

        step("file component", () -> {

            sleep(SLEEP_INTERVAL);
            clickOn("#file");
            sleep(SLEEP_INTERVAL);

            sleep(SLEEP_INTERVAL);
            clickOn("#file");
            sleep(SLEEP_INTERVAL);

            MenuItem menu = lookup("#openMenuItem").queryAs(ContextMenuContent.MenuItemContainer.class).getItem();
            assertTrue(menu.isVisible());
        });
    }
}
