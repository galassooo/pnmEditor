package org.supsi.view.preferences;

import com.sun.javafx.scene.control.ContextMenuContent;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.supsi.view.AbstractGUITest;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.util.WaitForAsyncUtils;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;

public class PreferencesPopupTest extends AbstractGUITest {

    @Override
    public void start(Stage stage) throws Exception {
        mockedFileChooser = mockConstruction(FileChooser.class,
                (mock, context) -> {
                    File testFile = new File(getClass().getResource("/image.ppm").getFile());


                    when(mock.showOpenDialog(any())).thenReturn(testFile);
                    when(mock.showSaveDialog(any())).thenReturn(testFile);
                });
        super.start(stage);
    }

    @Override
    public void stop() throws Exception {
        mockedFileChooser.close();
        super.stop();
    }


    @Test
    public void walkThrough() {
        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#root");
        openPreferences();
        selectFirstChoiceBoxItem();
        verifyComponents();
        savePreferences();

        //without language
        openPreferences();
        verifyComponents();
        savePreferences();
    }

    private void openPreferences() {
        step("preferences menu item", () -> {
            sleep(SLEEP_INTERVAL);
            clickOn("#edit");
            sleep(SLEEP_INTERVAL);

            Node menuContent = lookup("#preferences").query();
            MenuItem menuItem = ((ContextMenuContent.MenuItemContainer)menuContent).getItem();
            assertFalse(menuItem.isDisable());

            clickOn("#preferences");
            sleep(SLEEP_INTERVAL);

            verifyThat("#preferencesPopupRoot", NodeMatchers.isVisible());
        });
    }

    private void verifyComponents(){
        clickOn("#warningCB");
        sleep(SLEEP_INTERVAL);

        clickOn("#errorCB");
        sleep(SLEEP_INTERVAL);

        clickOn("#infoCB");
        sleep(SLEEP_INTERVAL);

        clickOn("#debugCB");
        sleep(SLEEP_INTERVAL);

    }

    private void selectFirstChoiceBoxItem() {
        step("select first choice box item", () -> {

            clickOn("#choiceBox");
            sleep(SLEEP_INTERVAL);
            moveBy(-20, 30); //hard select
            sleep(SLEEP_INTERVAL);
            clickOn();
        });
    }

    private void savePreferences() {
        step("close preferences menu", () -> {
            sleep(SLEEP_INTERVAL);
            clickOn("#preferencesPopupSave");

            sleep(SLEEP_INTERVAL);
        });
    }
}
