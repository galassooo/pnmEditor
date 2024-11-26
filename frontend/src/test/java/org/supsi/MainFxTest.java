package org.supsi;

import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationTest;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MainFxTest extends ApplicationTest {

    @BeforeAll
    public static void setupSpec() {
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            System.setProperty("testfx.robot", "glass");
            System.setProperty("prism.order", "sw");
            System.setProperty("java.awt.headless", "true");
        }
    }

    public void start(final Stage stage) throws Exception {
        MainFx mainFx = Mockito.spy(MainFx.class);

        doReturn(null).when(mainFx).getResource(anyString());
        assertThrows(Exception.class, ()->mainFx.start(stage));
    }

    @Test
    void emptyTest(){

    }
}
