package org.supsi;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MainTest {

    @Test
    public void testMain() {
        try(MockedStatic<MainFx> mainFxMockedStatic = mockStatic(MainFx.class)){
            mainFxMockedStatic.when(() -> MainFx.main(any())).thenAnswer(invocation -> null);

            Main.main(null);
            mainFxMockedStatic.verify(() -> MainFx.main(any()));
        }
    }
    @Test
    void testConstructor(){ //useless but needed for 100% coverage
        Main main = new Main();
    }
}
