package ch.supsi.dataaccess.image;

import ch.supsi.business.image.ImageAccess;
import ch.supsi.business.image.ImageDataAccess;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.lang.reflect.InvocationTargetException;

public class DataAccessFactory {
    private static final Map<String, Class<?>> imageMap = new HashMap<>();

    static {
        load();
    }

    /**
     * Il codice funziona sia con classi concrete che astratte, ovvero non si rompe
     * se metto l'annotazione su una classe astratta e questo perchè lavoro con l'interfaccia
     * e non con la classe concreta. Anche se usiamo una classe astratta come wrapper, la
     * classe ritornata dovrà per forza essere un istanza concreta in quanto non si possono
     * creare classi astratte in java
     *
     * --- caso che funziona (testato) ---
     * public class AbstractClass implements ImageDataAccess {
     *
     *     protected abstract ImageBusinessInterface read(String path) throws IOException;
     *     protected abstract ImageBusinessInterface write(ImageBusinessInterface image) throws IOException ;
     *
     *     public AbstractClass getInstance(){
     *         return new ConcreteChildClass();
     *     }
     * }
     * quindi in sostanza bisogna controllare che il return type del getInstance sia concreto.
     *
     *
     */
    private static void load(){
        imageMap.clear();
        //Create a new reflections invalidating cache (used for tests)
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .forPackages("ch.supsi")
                .addScanners(Scanners.TypesAnnotated, Scanners.SubTypes)
                .setExpandSuperTypes(false)
                .addUrls(ClasspathHelper.forPackage("ch.supsi.dataaccess.image"))
                .setExpandSuperTypes(false));

        //find all classes annotated with @ImageAccess
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(ImageAccess.class);

        for (Class<?> clazz : annotatedClasses) {
            if (ImageDataAccess.class.isAssignableFrom(clazz)) {
                ImageAccess annotation = clazz.getAnnotation(ImageAccess.class);
                String[] magicNumbers = annotation.magicNumber();


                for (String magicNumber : magicNumbers) {
                    imageMap.put(magicNumber, clazz);
                }
            }
        }
    }
    //obtain an instance by calling getInstance static method
    private static Object getSingletonInstance(Class<?> clazz) throws IllegalAccessException{
        try {
            //magari aggiungi un check sul tipo di ritorno che sia compatibile con l'interfaccia
            Method getInstanceMethod = clazz.getDeclaredMethod("getInstance");
            if (java.lang.reflect.Modifier.isStatic(getInstanceMethod.getModifiers())) {
                if(!java.lang.reflect.Modifier.isPublic(getInstanceMethod.getModifiers())) {
                    getInstanceMethod.setAccessible(true);
                }
                if(!ImageDataAccess.class.isAssignableFrom(getInstanceMethod.getReturnType())) {
                    throw new IllegalAccessException();
                }
                return getInstanceMethod.invoke(null);

            }else {
                throw new IllegalAccessException();
            }
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw  new IllegalAccessException("Class marked with @ImageAccess must provide a singleton static access (getInstance) with a concrete class implementing ImageDataAccess as return type" + clazz.getName());
        } catch (InvocationTargetException e) {
            throw new IllegalAccessException("Singleton method thrown an exception: " + e.getTargetException().getMessage());
        }
    }

    public static ImageDataAccess getInstance(String path) throws IOException, IllegalAccessException {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {


            String firstLine = reader.readLine();

            while (firstLine.isEmpty()) { //skip empty line
                firstLine = reader.readLine();
            }
            if(!imageMap.containsKey(firstLine.trim())){
                    throw new IOException("Unsupported file type");
            }
            Class<?> clazz = imageMap.get(firstLine.trim());

            return loadClazz(clazz);
        }
    }

    public static ImageDataAccess getInstanceFromMagicNumber(String magicNumber) throws IOException, IllegalAccessException {
            if(!imageMap.containsKey(magicNumber)){
                throw new IOException("Unsupported file type");
            }
            Class<?> clazz = imageMap.get(magicNumber);
            return loadClazz(clazz);
    }

    private static ImageDataAccess loadClazz(Class<?> clazz) throws IllegalAccessException {
        ImageDataAccess instance;
        try {
            instance = (ImageDataAccess) getSingletonInstance(clazz);
            if(instance == null){
                throw new IllegalAccessException("Cannot access a null class");
            }
        } catch (IllegalAccessException e) {
            throw new IllegalAccessException(e.getMessage());
        }
        return instance;
    }
}
