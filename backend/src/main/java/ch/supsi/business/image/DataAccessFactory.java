package ch.supsi.business.image;


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
    private static void load(){
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
            Method getInstanceMethod = clazz.getDeclaredMethod("getInstance");
            if (java.lang.reflect.Modifier.isStatic(getInstanceMethod.getModifiers())) {
                if(!java.lang.reflect.Modifier.isPublic(getInstanceMethod.getModifiers())) {
                    getInstanceMethod.setAccessible(true);
                }
                return getInstanceMethod.invoke(null);

            }else {
                throw new IllegalAccessException();
            }
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw  new IllegalAccessException("Class marked with @ImageAccess must provide a singleton static access (getInstance)" + clazz.getName());
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

            ImageDataAccess instance;
            try {
                instance = (ImageDataAccess) getSingletonInstance(clazz);
            } catch (IllegalAccessException e) {
                throw new IllegalAccessException(e.getMessage());
            }
            return instance;
        }
    }
}
