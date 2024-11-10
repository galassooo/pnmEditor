package ch.supsi.dataaccess.image;

import ch.supsi.DataAccessComponent;
import ch.supsi.annotation.ImageAccessFactory;
import ch.supsi.annotation.Register;
import ch.supsi.business.image.ImageDataAccess;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.lang.reflect.InvocationTargetException;

@ImageAccessFactory
public class DataAccessFactory {


    @Register
    private static List<DataAccessComponent> ciaone;

    //obtain an instance by calling getInstance static method
    private static Object getSingletonInstance(Class<?> clazz) throws IllegalAccessException {
        try {
            //magari aggiungi un check sul tipo di ritorno che sia compatibile con l'interfaccia
            Method getInstanceMethod = clazz.getDeclaredMethod("getInstance");
            if (!java.lang.reflect.Modifier.isPublic(getInstanceMethod.getModifiers())) {
                getInstanceMethod.setAccessible(true);
            }
            return getInstanceMethod.invoke(null);

        } catch (NoSuchMethodException | IllegalAccessException ignored) {
            return null; //impossible exception,  singleton integrity in plugin
        } catch (InvocationTargetException e) {
            throw new IllegalAccessException("Singleton method thrown an exception.");
        }
    }

    public static ImageDataAccess getInstance(String path) throws IOException, IllegalAccessException {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {


            String firstLine = reader.readLine();

            while (firstLine.isEmpty()) { //skip empty line
                firstLine = reader.readLine();
            }
            return getInstanceFromMagicNumber(firstLine);
        }
    }

    public static ImageDataAccess getInstanceFromMagicNumber(String magicNumber) throws IOException, IllegalAccessException {
        Class<?> clazz = getFromMagicNumber(magicNumber);
        if (clazz == null) {
            throw new IOException("Unsupported file type");
        }
        return loadClazz(clazz);
    }

    public static ImageDataAccess getInstanceFromExtension(String extension) throws IOException, IllegalAccessException {
        Class<?> clazz = getFromExtension(extension);
        if (clazz == null) {
            throw new IOException("Unsupported file type");
        }
        return loadClazz(clazz);
    }

    private static ImageDataAccess loadClazz(Class<?> clazz) throws IllegalAccessException {
        ImageDataAccess instance;
        try {
            instance = (ImageDataAccess) getSingletonInstance(clazz);
        } catch (IllegalAccessException e) {
            throw new IllegalAccessException(e.getMessage());
        }
        return instance;
    }

    private static Class<?> getFromMagicNumber(String magicNumber) {
        for(DataAccessComponent component : ciaone) {
            if(Arrays.asList(component.magicNumber).contains(magicNumber)) {
                return component.clazz;
            }
        }
        return null;
    }

    private static Class<?> getFromExtension(String extension) {
        for(DataAccessComponent component : ciaone) {
            if(component.extension.equals(extension)) {
                return component.clazz;
            }
        }
        return null;
    }

    public static List<String> getSupportedExtensions() {
        return ciaone.stream().map(e -> e.extension).toList();
    }

    public static String getDefaultMagicNumberFromExtension(String extension) {
        for(DataAccessComponent component : ciaone) {
            if(component.extension.equals(extension)) {
                return component.magicNumber[0];
            }
        }
        return null;
    }
}
