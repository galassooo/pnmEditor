package ch.supsi.dataaccess.image;

import ch.supsi.DataAccessComponent;
import ch.supsi.ImageAccess;
import ch.supsi.business.image.ImageDataAccess;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.lang.reflect.InvocationTargetException;

public class DataAccessFactory {
    static {

    }


    private static List<DataAccessComponent> dataAccesses;

    //obtain an instance by calling getInstance static method
    private static Object getSingletonInstance(Class<?> clazz) throws IllegalAccessException {
        try {
            //magari aggiungi un check sul tipo di ritorno che sia compatibile con l'interfaccia
            Method getInstanceMethod = clazz.getDeclaredMethod("getInstance");
            if (!java.lang.reflect.Modifier.isPublic(getInstanceMethod.getModifiers())) {
                getInstanceMethod.setAccessible(true);
            }
            return getInstanceMethod.invoke(null);

        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new IllegalAccessException("Class marked with @ImageAccess must provide a singleton static access (getInstance) with a concrete class implementing ImageDataAccess as return type");
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
        for(DataAccessComponent component : dataAccesses) {
            if(Arrays.asList(component.magicNumber).contains(magicNumber)) {
                return component.clazz;
            }
        }
        return null;
    }

    private static Class<?> getFromExtension(String extension) {
        for(DataAccessComponent component : dataAccesses) {
            if(component.extension.equals(extension)) {
                return component.clazz;
            }
        }
        return null;
    }
}
