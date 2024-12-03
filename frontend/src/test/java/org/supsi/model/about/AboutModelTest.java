package org.supsi.model.about;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class AboutModelTest {

    IAboutModel model = AboutModel.getInstance();

    @Test
    public void testSetDate(){
        String dateValue = "Date";
        model.setDate(dateValue);
        assertEquals(dateValue, model.getDate());
    }

    @Test
    public void testSetVersion(){
        String versionValue = "Version";
        model.setVersion(versionValue);
        assertEquals(versionValue, model.getVersion());
    }

    @Test
    public void testSetDev(){
        String devValue = "Developer";
        model.setDeveloper(devValue);
        assertEquals(devValue, model.getDeveloper());
    }

    @Test
    void testEqualsSameObject(){
        assertEquals(model, model);
        assertEquals(model.hashCode(), model.hashCode());
    }
    
    @Test
    void testEqualsDifferentClass(){
        assertNotEquals(model, "");
    }
    @Test
    void testSingleton(){
        assertEquals(AboutModel.getInstance(), AboutModel.getInstance());
    }
}
