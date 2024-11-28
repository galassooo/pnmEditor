package org.supsi.view.info;

import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.supsi.model.info.LoggerModel;
import org.supsi.model.translations.TranslationModel;
import org.supsi.view.AbstractGUITest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

public class LoggerViewTest extends AbstractGUITest {

    MockedStatic<TranslationModel> modelStaticMock;
    @Override
    public void start(Stage stage) throws Exception {
        modelStaticMock = mockStatic(TranslationModel.class);
        TranslationModel modelMock = spy(TranslationModel.class);

        when(modelMock.translate("")).thenReturn("");

        modelStaticMock.when(TranslationModel::getInstance).thenReturn(modelMock);

        super.start(stage);
    }

    @Override
    public void stop() throws Exception {
        modelStaticMock.close();
        super.stop();
    }

    @Test
    void walkthrough(){
        clickOn("#root");
        Platform.runLater(() -> {
            assertDoesNotThrow(() -> LoggerModel.getInstance().addInfo(""));
            assertDoesNotThrow(() -> LoggerModel.getInstance().clear());
        });
    }
}
