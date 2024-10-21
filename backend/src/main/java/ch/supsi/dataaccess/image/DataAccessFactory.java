package ch.supsi.dataaccess.image;


import ch.supsi.business.image.ImageAccess;
import ch.supsi.business.image.ImageDataAccess;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
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
    private static final Map<String, ImageDataAccess> imageMap = new HashMap<>();

    static {
        //FUNZIONA SIA DA JAR CHE DA IDE :) :) :) ed è molto veloce :) :)

            Reflections reflections = new Reflections(new ConfigurationBuilder()
                    .forPackage("ch.supsi.dataaccess.image")
                    .addScanners(Scanners.TypesAnnotated, Scanners.SubTypes));

            Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(ImageAccess.class);

            for (Class<?> clazz : annotatedClasses) {
                if (ImageDataAccess.class.isAssignableFrom(clazz)) {
                    ImageAccess annotation = clazz.getAnnotation(ImageAccess.class);
                    String[] magicNumber = annotation.magicNumber();

                    ImageDataAccess instance = (ImageDataAccess) getSingletonInstance(clazz); //checked cast
                    if (instance != null) {
                        for (String code : magicNumber) {
                            imageMap.put(code, instance);
                        }
                    }

                } else {
                    //throw ex: classe annotata con imageDataAccess non implementa interface
                }
            }
        imageMap.forEach((k, v) -> {
            System.out.println(k + v.getClass().getName());
        });
        checkWrongPlacedFiles();
    }

    //ottiene singleton invocando il metodo getInstance()
    private static Object getSingletonInstance(Class<?> clazz){
        try {
            Method getInstanceMethod = clazz.getDeclaredMethod("getInstance");
            if (java.lang.reflect.Modifier.isStatic(getInstanceMethod.getModifiers())) {
                return getInstanceMethod.invoke(null);
            }
        } catch (NoSuchMethodException e) {
            System.err.println("Class marked with @ImageAccess must implement a singleton constructor" + clazz.getName());
        }catch (InvocationTargetException | IllegalAccessException e ){
            System.err.println();
        }
        return null;
    }

    private static void checkWrongPlacedFiles() {
        // Configura Reflections per cercare tutte le classi annotate in "ch.supsi"
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .forPackage("ch.supsi")
                .addScanners(Scanners.TypesAnnotated, Scanners.SubTypes));

        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(ImageAccess.class);

        for (Class<?> clazz : annotatedClasses) {
            String packageName = clazz.getPackage().getName();
            if (!packageName.startsWith("ch.supsi.dataaccess.image")) {
                System.out.println("The class '" + clazz.getSimpleName() + "' is annotated with @ImageAccess but is located outside the 'ch.supsi.dataaccess.image' package. " +
                        "This class will be ignored for read/write operations. " +
                        "To enable its use for image reading and writing, please move it to the 'ch.supsi.dataaccess.image' package.");

            }
        }
    }

    public static ImageDataAccess getInstance(String path) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String firstLine = reader.readLine();

            while(firstLine == null ||  firstLine.isEmpty()) { //skip empty line
                firstLine = reader.readLine();
            }
            ImageDataAccess instance =  imageMap.get(firstLine.trim());
            if(instance == null){
                throw new IOException("Unsupported file type");
            }
            return instance;
        }
    }
}
