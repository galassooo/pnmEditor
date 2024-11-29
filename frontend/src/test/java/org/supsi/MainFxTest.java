package org.supsi;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.supsi.controller.exit.ExitController;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MainFxTest extends ApplicationTest {

    @BeforeAll
    public static void setupSpec() {
        System.setProperty("testfx.robot", "glass");
        System.setProperty("prism.order", "sw");
        System.setProperty("java.awt.headless", "true");
    }

    public void start(final Stage stage) throws Exception {
        MainFx mainFx = Mockito.spy(MainFx.class);

        doReturn(null).when(mainFx).getResource(anyString());
        assertThrows(Exception.class, () -> mainFx.start(stage));
    }

    @Test
    void emptyTest() {
        //trigger start
    }
}

class MainFXMissingResourcesTest extends ApplicationTest {

    @BeforeAll
    public static void setupSpec() {
        System.setProperty("testfx.robot", "glass");
        System.setProperty("prism.order", "sw");
        System.setProperty("java.awt.headless", "true");
    }

    public void start(final Stage stage) throws Exception {
        MainFx mainFx = Mockito.spy(MainFx.class);

        doReturn(null).when(mainFx).getResource("/layout/ExportMenu.fxml");
        doReturn(null).when(mainFx).getResource("/layout/FilterMenu.fxml");
        doReturn(null).when(mainFx).getResource("/layout/Image.fxml");
        doReturn(null).when(mainFx).getResource("/layout/HistoryColumn.fxml");

        assertDoesNotThrow(() -> mainFx.start(stage));
    }

    @Test
    void emptyTest() {
        //trigger start
    }
}

class MainFXMissingInfoColTest extends ApplicationTest {

    @BeforeAll
    public static void setupSpec() {
        System.setProperty("testfx.robot", "glass");
        System.setProperty("prism.order", "sw");
        System.setProperty("java.awt.headless", "true");
    }

    public void start(final Stage stage) throws Exception {
        MainFx mainFx = Mockito.spy(MainFx.class);

        doReturn(null).when(mainFx).getResource("/layout/InfoColumn.fxml");

        assertDoesNotThrow(() -> mainFx.start(stage));
    }

    @Test
    void emptyTest() {
        //trigger start
    }
}

class MainFxMainMethodTest {
    @Test
    void testMain() throws InterruptedException {
        //count down latch per quando il launch termina
        CountDownLatch latch = new CountDownLatch(1);

        //altro thread -> altrimenti non posso joinare
        Thread applicationThread = new Thread(() -> {
            try {
                //chiamo main fx
                MainFx.main(new String[]{});
            } catch (Exception ignored) {
            }
        });

        applicationThread.start();

        //do tempo all applicazione di avviarsi
        Thread.sleep(2000);

        //chiudo l'applicazione e segnalo con count down latch che ha terminato
        Platform.runLater(() -> {
            Platform.exit();
            latch.countDown();
        });

        //chiudo main thread, aspetto max 30 secondi altrimenti
        //fallisco test (piu che sufficienti)
        assertTrue(latch.await(30, TimeUnit.SECONDS));
    }
}

class MainFxCloseRequestTest extends ApplicationTest {

    private Stage stage;
    private MockedStatic<Platform> mockedStatic;

    @BeforeAll
    public static void setupSpec() {
        System.setProperty("testfx.robot", "glass");
        System.setProperty("prism.order", "sw");
        System.setProperty("java.awt.headless", "true");
    }

    @Override
    public void start(final Stage stage) throws Exception {

        mockedStatic = mockStatic(Platform.class);

        mockedStatic.when(Platform::exit).thenAnswer(invocation -> {
            return null;
        });

        MainFx mainFx = new MainFx();
        this.stage = stage;
        mainFx.start(stage);
        stage.toFront();
    }
    @Override
    public void stop() throws Exception {
        mockedStatic.close();
        super.stop();
    }

    @Test
    void testCloseRequest() {
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#root");

        Platform.runLater(() -> {
            WindowEvent closeEvent = new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST);
            Event.fireEvent(stage, closeEvent);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
}
