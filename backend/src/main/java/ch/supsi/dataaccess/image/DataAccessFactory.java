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

/**
 * Factory class for managing and instantiating image data access components.
 * This class uses registered {@link DataAccessComponent} entries to provide instances
 * of image data access implementations based on file extension or magic number.
 * @see ImageDataAccess
 */
@ImageAccessFactory
public class DataAccessFactory {

    /**
     * {@link List} of registered data access components.
     */
    @Register
    private static List<DataAccessComponent> dataAccessComponents;

    /**
     * Retrieves a list of supported file extensions from the registered data access components.
     *
     * @return a {@link List} of supported file extensions
     */
    public static List<String> getSupportedExtensions() {
        return dataAccessComponents.stream().map(e -> e.extension).toList();
    }

    /**
     * Retrieves the default magic number associated with a given file extension.
     *
     * @param extension the file extension
     * @return an {@link Optional} containing the default magic number, or empty if not found
     */
    public static Optional<String> getDefaultMagicNumberFromExtension(String extension) {
        for (DataAccessComponent component : dataAccessComponents) {
            if (component.extension.equals(extension)) {
                return Optional.of(component.magicNumber[0]);
            }
        }
        return Optional.empty();
    }

    /**
     * Creates an instance of {@link ImageDataAccess} for a given file path.
     * The factory determines the appropriate implementation by reading the file's magic number.
     *
     * @param path the path to the image file
     * @return an instance of {@link ImageDataAccess}
     * @throws IOException            if the file cannot be read or the file type is unsupported
     * @throws IllegalAccessException if an error occurs while creating the instance
     */
    public static ImageDataAccess getInstance(String path) throws IOException, IllegalAccessException {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {


            String firstLine = reader.readLine();

            while (firstLine.isEmpty()) { //skip empty line
                firstLine = reader.readLine();
            }
            return getInstanceFromMagicNumber(firstLine);
        }
    }

    /**
     * Creates an instance of {@link ImageDataAccess} based on the provided magic number.
     *
     * @param magicNumber the magic number of the image file
     * @return an instance of {@link ImageDataAccess}
     * @throws IOException            if the magic number is unsupported
     * @throws IllegalAccessException if an error occurs while creating the instance
     */
    public static ImageDataAccess getInstanceFromMagicNumber(String magicNumber) throws IOException, IllegalAccessException {
        Class<?> clazz = getFromMagicNumber(magicNumber);
        if (clazz == null) {
            throw new IOException("Unsupported file type");
        }
        return loadClazz(clazz);
    }

    /**
     * Creates an instance of {@link ImageDataAccess} based on the provided file extension.
     *
     * @param extension the file extension
     * @return an instance of {@link ImageDataAccess}
     * @throws IOException            if the file extension is unsupported
     * @throws IllegalAccessException if an error occurs while creating the instance
     */
    public static ImageDataAccess getInstanceFromExtension(String extension) throws IOException, IllegalAccessException {
        Class<?> clazz = getFromExtension(extension);
        if (clazz == null) {
            throw new IOException("Unsupported file type");
        }
        return loadClazz(clazz);
    }

    /**
     * Retrieves the singleton instance of a class by invoking its `getInstance` method.
     *
     * @param clazz the class to retrieve the instance from
     * @return the singleton instance of the class
     * @throws IllegalAccessException if the `getInstance` method throws an exception or is inaccessible
     */
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

    /**
     * Loads an instance of {@link ImageDataAccess} from a class.
     *
     * @param clazz the class to load
     * @return an instance of {@link ImageDataAccess}
     * @throws IllegalAccessException if an error occurs while creating the instance
     */
    private static ImageDataAccess loadClazz(Class<?> clazz) throws IllegalAccessException {
        ImageDataAccess instance;
        try {
            instance = (ImageDataAccess) getSingletonInstance(clazz);
        } catch (IllegalAccessException e) {
            throw new IllegalAccessException(e.getMessage());
        }
        return instance;
    }

    /**
     * Retrieves the class corresponding to the given magic number from the registered components.
     *
     * @param magicNumber the magic number to search for
     * @return the {@link Class} associated with the magic number, or null if not found
     */
    private static Class<?> getFromMagicNumber(String magicNumber) {
        for (DataAccessComponent component : dataAccessComponents) {
            if (Arrays.asList(component.magicNumber).contains(magicNumber)) {
                return component.clazz;
            }
        }
        return null;
    }

    /**
     * Retrieves the class corresponding to the given file extension from the registered components.
     *
     * @param extension the file extension to search for
     * @return the {@link Class} associated with the file extension, or null if not found
     */
    private static Class<?> getFromExtension(String extension) {
        for (DataAccessComponent component : dataAccessComponents) {
            if (component.extension.equals(extension)) {
                return component.clazz;
            }
        }
        return null;
    }
}
