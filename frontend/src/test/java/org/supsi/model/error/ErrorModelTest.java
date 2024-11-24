package org.supsi.model.error;

import org.junit.jupiter.api.Test;
import org.supsi.model.errors.ErrorModel;
import org.supsi.model.errors.IErrorModel;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ErrorModelTest {

    IErrorModel model = ErrorModel.getInstance();

    @Test
    void testSingleton(){
        assertEquals(model, ErrorModel.getInstance());
    }

    @Test
    void testMessage(){
        String message = "message";
        model.setMessage(message);
        assertEquals(message, model.getMessage());
    }
}
