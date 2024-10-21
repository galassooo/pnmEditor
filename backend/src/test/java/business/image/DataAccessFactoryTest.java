package business.image;

import ch.supsi.business.image.DataAccessFactory;
import ch.supsi.business.image.ImageDataAccess;
import ch.supsi.dataaccess.image.PBMDataAccess;
import ch.supsi.dataaccess.image.PGMDataAccess;
import ch.supsi.dataaccess.image.PPMDataAccess;
import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.StringMemberValue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class DataAccessFactoryTest {

    @TempDir
    Path tempDir;

    /* ----------- classes -------------*/

    @Test //lascialo commentato che è difficile da capire grazie! :)
    public void testGenerateDynamicClass() throws Exception {
        String magicNumber = "TEST";
        //crate temporary file with TEST magicNumber
        Path tempFile = tempDir.resolve("image.pgm");
        String asciiData = "\n"+magicNumber+"\n";
        Files.write(tempFile, asciiData.getBytes());


        //retrieve the default classPool (dalla javadoc):
        /*
         * The default class pool searches the system search path,
         * which usually includes the platform library, extension libraries,
         * and the search path specified by the -classpath option or the CLASSPATH
         * environment variable.
         */
        ClassPool pool = ClassPool.getDefault();
        pool.importPackage("ch.supsi.dataaccess.image");
        pool.importPackage("ch.supsi.business.image");
        pool.importPackage("ch.supsi.application.image");


        try { //safety check per quando si fa run all
            Class.forName("ch.supsi.dataaccess.image.WorkingClass");
            System.out.println("Dynamic test class found in VM");

        } catch (ClassNotFoundException e) {
            System.out.println("Dynamic test class not found in VM... Creating an instance...");
            CtClass dynamicClass = writeWorkingClass(pool, magicNumber);
            //converto in una classe java e la carico: da questo momento non posso modificare il codice!
            Class<?> clazz = dynamicClass.toClass();
            dynamicClass.defrost();
        }

        assertDoesNotThrow(() -> {
            ImageDataAccess instance = DataAccessFactory.getInstance(tempFile.toAbsolutePath().toString());
            assertNotNull(instance);
            System.out.println("classe dinamica found: " + instance.getClass().getName());
        });
    }

    private CtClass writeWorkingClass(ClassPool pool, String magicNumber) throws CannotCompileException, IOException, NotFoundException {

        //create a class ATTENZIONE: il metodo non compila ne salva la classe nel package!
        //crea la struttura per poter essere modificata prima di essere compilata e salvata
        CtClass dynamicClass = pool.makeClass("ch.supsi.dataaccess.image.WorkingClass");

        addInterfaceToDynClass(dynamicClass, pool);
        addConstructorToDynClass(dynamicClass);

        //create singleton method
        CtMethod getInstanceMethod = CtMethod.make(
                "public static WorkingClass getInstance() { return new WorkingClass(); }",
                dynamicClass
        );
        dynamicClass.addMethod(getInstanceMethod);

        addAnnotationToDynClass(dynamicClass, magicNumber);

        String outputDirectory = "target/classes"; //output dir per la classe
        dynamicClass.writeFile(outputDirectory);
        dynamicClass.defrost();
        return dynamicClass;
    }

    void addAnnotationToDynClass(CtClass dynamicClass, String magicNumber) {

        //add @ImageAccess annotation via bytecode
        ConstPool constPool = dynamicClass.getClassFile().getConstPool();
        AnnotationsAttribute attr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag); //runtime visible annotation
        //aggiunge annotation
        javassist.bytecode.annotation.Annotation imageAccessAnnotation = new javassist.bytecode.annotation.Annotation("ch.supsi.business.image.ImageAccess", constPool);

        ArrayMemberValue arrayMemberValue = new ArrayMemberValue(constPool);
        //aggiunge il valore Test come array all'annotation (singolo parametro => non serve nome campo)
        StringMemberValue stringValue = new StringMemberValue(magicNumber, constPool);
        arrayMemberValue.setValue(new StringMemberValue[]{stringValue});

        imageAccessAnnotation.addMemberValue("magicNumber", arrayMemberValue);
        attr.addAnnotation(imageAccessAnnotation);

        dynamicClass.getClassFile().addAttribute(attr); //aggiungo annotation
    }

    void addInterfaceToDynClass(CtClass dynamicClass, ClassPool pool) throws CannotCompileException, NotFoundException {
        //per qualche strana ragione compila anche se non
        //implemento i metodi dell'interfaccia, ma li lascio comunque qui
        //sia mai che fixano sto bug
        dynamicClass.addInterface(pool.get(ImageDataAccess.class.getName()));

        //overridden method
        CtMethod readImageMethod = CtMethod.make(
                "public ImageBusinessInterface read(String path) { return new ImageBusiness(null, \"\", \"TEST\", null); }",
                dynamicClass
        );
        dynamicClass.addMethod(readImageMethod);

        //overridden method
        CtMethod writeImageMethod = CtMethod.make(
                "public ImageBusinessInterface write(ImageBusinessInterface img ) { return new ImageBusiness(null, \"\", \"TEST\", null); }",
                dynamicClass
        );
        dynamicClass.addMethod(writeImageMethod);
    }

    void addConstructorToDynClass(CtClass dynamicClass) throws CannotCompileException {
        //add constructor to the class
        CtConstructor defaultConstructor = new CtConstructor(new CtClass[]{}, dynamicClass);

        //set the constructor body
        defaultConstructor.setBody("{}");
        dynamicClass.addConstructor(defaultConstructor);

    }

    @Test
    void testPPM() throws IOException, IllegalAccessException {
        tempDir.resolve("file.ppm");
        Path tempFile = tempDir.resolve("image.ppm");
        String asciiData = "P3\n2 2\n65535\n65535 0 0\n0 0 65535\n0 0 0\n65535 65535 65535\n";
        Files.write(tempFile, asciiData.getBytes());

        ImageDataAccess dac = DataAccessFactory.getInstance(tempFile.toAbsolutePath().toString());
        assertEquals(dac, PPMDataAccess.getInstance());
    }

    @Test
    void testPBM() throws IOException, IllegalAccessException {
        Path tempFile = tempDir.resolve("image.pbm");
        String asciiData = "P1\n2 2\n1 0\n0 1\n";
        Files.write(tempFile, asciiData.getBytes());

        ImageDataAccess dac = DataAccessFactory.getInstance(tempFile.toAbsolutePath().toString());

        assertEquals(dac, PBMDataAccess.getInstance());
    }

    @Test
    void testPGM() throws IOException, IllegalAccessException {
        Path tempFile = tempDir.resolve("image.pgm");
        String asciiData = "P2\n2 2\n65535\n65535 0 0\n0 0 65535\n0 0 0\n65535 65535 65535\n";
        Files.write(tempFile, asciiData.getBytes());

        ImageDataAccess dac = DataAccessFactory.getInstance(tempFile.toAbsolutePath().toString());

        assertEquals(dac, PGMDataAccess.getInstance());
    }
    /* ----------- various input -------------*/

    @Test
    void testEmptyLines() throws IOException, IllegalAccessException {
        Path tempFile = tempDir.resolve("image.pgm");
        String asciiData = "\nP2\n2 2\n65535\n65535 0 0\n0 0 65535\n0 0 0\n65535 65535 65535\n";
        Files.write(tempFile, asciiData.getBytes());

        ImageDataAccess dac = DataAccessFactory.getInstance(tempFile.toAbsolutePath().toString());

        assertEquals(dac, PGMDataAccess.getInstance());
    }

    /* ----------- unsupported data -------------*/
    @Test
    void testUnsupported() throws IOException {
        Path tempFile = tempDir.resolve("image.pgm");
        String asciiData = "\n37163743\n2 2\n65535\n65535 0 0\n0 0 65535\n0 0 0\n65535 65535 65535\n";
        Files.write(tempFile, asciiData.getBytes());

        IOException e = assertThrows(IOException.class, () -> DataAccessFactory.getInstance(tempFile.toAbsolutePath().toString()));
        assertEquals("Unsupported file type", e.getMessage());
    }

    /* ------------- invalid input ------------*/

    @Test
    public void testNoSingleton() throws Exception {
        String magicNumber = "TEST1";

        Path tempFile = tempDir.resolve("image.pgm");
        String asciiData = "\n"+magicNumber+"\n";
        Files.write(tempFile, asciiData.getBytes());

        ClassPool pool = ClassPool.getDefault();
        pool.importPackage("ch.supsi.dataaccess.image");
        pool.importPackage("ch.supsi.business.image");
        pool.importPackage("ch.supsi.application.image");


        try { //safety check per quando si fa run all
            Class.forName("ch.supsi.dataaccess.image.NoSingletonClass");
            System.out.println("Dynamic test class found in VM");

        } catch (ClassNotFoundException e) {
            System.out.println("Dynamic test class not found in VM... Creating an instance...");
            CtClass dynamicClass = writeNoSingleton(pool, magicNumber);
            Class<?> clazz = dynamicClass.toClass();
            dynamicClass.defrost();
        }
        assertThrows(IllegalAccessException.class, () -> DataAccessFactory.getInstance(tempFile.toAbsolutePath().toString()));
    }

    private CtClass writeNoSingleton(ClassPool pool, String magicNumber) throws CannotCompileException, IOException, NotFoundException {

        CtClass dynamicClass = pool.makeClass("ch.supsi.dataaccess.image.NoSingletonClass");

        addInterfaceToDynClass(dynamicClass, pool);
        addConstructorToDynClass(dynamicClass);
        addAnnotationToDynClass(dynamicClass, magicNumber);

        String outputDirectory = "target/classes";
        dynamicClass.writeFile(outputDirectory);
        dynamicClass.defrost();
        return dynamicClass;
    }

    @Test
    public void testNoAnnotation() throws Exception {
        String magicNumber = "TEST2";

        Path tempFile = tempDir.resolve("image.pgm");
        String asciiData = "\n"+magicNumber+"\n";
        Files.write(tempFile, asciiData.getBytes());

        ClassPool pool = ClassPool.getDefault();
        pool.importPackage("ch.supsi.dataaccess.image");
        pool.importPackage("ch.supsi.business.image");
        pool.importPackage("ch.supsi.application.image");


        try { //safety check per quando si fa run all
            Class.forName("ch.supsi.dataaccess.image.NoAnnotationClass");
            System.out.println("Dynamic test class found in VM");

        } catch (ClassNotFoundException e) {
            System.out.println("Dynamic test class not found in VM... Creating an instance...");
            CtClass dynamicClass = writeNoAnnotation(pool, magicNumber);
            Class<?> clazz = dynamicClass.toClass();
            dynamicClass.defrost();
        }
        IOException e = assertThrows(IOException.class, () -> DataAccessFactory.getInstance(tempFile.toAbsolutePath().toString()));
        assertEquals("Unsupported file type", e.getMessage());
    }

    private CtClass writeNoAnnotation(ClassPool pool, String magicNumber) throws CannotCompileException, IOException, NotFoundException {

        CtClass dynamicClass = pool.makeClass("ch.supsi.dataaccess.image.NoAnnotationClass");

        addInterfaceToDynClass(dynamicClass, pool);
        addConstructorToDynClass(dynamicClass);

        CtMethod getInstanceMethod = CtMethod.make(
                "public static NoAnnotationClass getInstance() { return new NoAnnotationClass(); }",
                dynamicClass
        );
        dynamicClass.addMethod(getInstanceMethod);

        String outputDirectory = "target/classes";
        dynamicClass.writeFile(outputDirectory);
        dynamicClass.defrost();
        return dynamicClass;
    }

    @Test
    public void testNonStaticSingleton() throws Exception {
        String magicNumber = "TEST3";

        Path tempFile = tempDir.resolve("image.pgm");
        String asciiData = "\n"+magicNumber+"\n";
        Files.write(tempFile, asciiData.getBytes());

        ClassPool pool = ClassPool.getDefault();
        pool.importPackage("ch.supsi.dataaccess.image");
        pool.importPackage("ch.supsi.business.image");
        pool.importPackage("ch.supsi.application.image");


        try { //safety check per quando si fa run all
            Class.forName("ch.supsi.dataaccess.image.NonStaticSingletonClass");
            System.out.println("Dynamic test class found in VM");

        } catch (ClassNotFoundException e) {
            System.out.println("Dynamic test class not found in VM... Creating an instance...");
            CtClass dynamicClass = writeNonStaticSingleton(pool, magicNumber);
            Class<?> clazz = dynamicClass.toClass();
            dynamicClass.defrost();
        }
        IllegalAccessException e = assertThrows(IllegalAccessException.class, () -> DataAccessFactory.getInstance(tempFile.toAbsolutePath().toString()));
        assertTrue(e.getMessage().contains("Class marked with @ImageAccess must provide a singleton static access (getInstance)"));
    }

    private CtClass writeNonStaticSingleton(ClassPool pool, String magicNumber) throws CannotCompileException, IOException, NotFoundException {

        CtClass dynamicClass = pool.makeClass("ch.supsi.dataaccess.image.NonStaticSingletonClass");

        addInterfaceToDynClass(dynamicClass, pool);
        addConstructorToDynClass(dynamicClass);

        CtMethod getInstanceMethod = CtMethod.make(
                "public NonStaticSingletonClass getInstance() { return new NonStaticSingletonClass(); }",
                dynamicClass
        );
        dynamicClass.addMethod(getInstanceMethod);
        addAnnotationToDynClass(dynamicClass, magicNumber);


        String outputDirectory = "target/classes";
        dynamicClass.writeFile(outputDirectory);
        dynamicClass.defrost();
        return dynamicClass;
    }
    @Test
    public void testExceptionSingleton() throws Exception {
        String magicNumber = "TEST4";

        Path tempFile = tempDir.resolve("image.pgm");
        String asciiData = "\n"+magicNumber+"\n";
        Files.write(tempFile, asciiData.getBytes());

        ClassPool pool = ClassPool.getDefault();
        pool.importPackage("ch.supsi.dataaccess.image");
        pool.importPackage("ch.supsi.business.image");
        pool.importPackage("ch.supsi.application.image");
        pool.importPackage("import java.io.IOException;");


        try { //safety check per quando si fa run all
            Class.forName("ch.supsi.dataaccess.image.ExceptionSingletonClass");
            System.out.println("Dynamic test class found in VM");

        } catch (ClassNotFoundException e) {
            System.out.println("Dynamic test class not found in VM... Creating an instance...");
            CtClass dynamicClass = writeExceptionSingleton(pool, magicNumber);
            Class<?> clazz = dynamicClass.toClass();
            dynamicClass.defrost();
        }
        IllegalAccessException e = assertThrows(IllegalAccessException.class, () -> DataAccessFactory.getInstance(tempFile.toAbsolutePath().toString()));
        assertTrue(e.getMessage().contains("Singleton method thrown an exception: "));
    }

    private CtClass writeExceptionSingleton(ClassPool pool, String magicNumber) throws CannotCompileException, IOException, NotFoundException {

        CtClass dynamicClass = pool.makeClass("ch.supsi.dataaccess.image.ExceptionSingletonClass");

        addInterfaceToDynClass(dynamicClass, pool);
        addConstructorToDynClass(dynamicClass);

        CtMethod getInstanceMethod = CtMethod.make(
                "public static ExceptionSingletonClass getInstance() throws java.io.IOException { throw new java.io.IOException(); }",
                dynamicClass
        );
        dynamicClass.addMethod(getInstanceMethod);
        addAnnotationToDynClass(dynamicClass, magicNumber);


        String outputDirectory = "target/classes";
        dynamicClass.writeFile(outputDirectory);
        dynamicClass.defrost();
        return dynamicClass;
    }


    @Test
    public void testNonImplementedInterface() throws Exception {
        String magicNumber = "TEST5";

        Path tempFile = tempDir.resolve("image.pgm");
        String asciiData = "\n"+magicNumber+"\n";
        Files.write(tempFile, asciiData.getBytes());

        ClassPool pool = ClassPool.getDefault();
        pool.importPackage("ch.supsi.dataaccess.image");
        pool.importPackage("ch.supsi.business.image");
        pool.importPackage("ch.supsi.application.image");
        pool.importPackage("import java.io.IOException;");


        try { //safety check per quando si fa run all
            Class.forName("ch.supsi.dataaccess.image.NonImplementedInterfaceClass");
            System.out.println("Dynamic test class found in VM");

        } catch (ClassNotFoundException e) {
            System.out.println("Dynamic test class not found in VM... Creating an instance...");
            CtClass dynamicClass = writeNonImplementedInterfaceClass(pool, magicNumber);
            Class<?> clazz = dynamicClass.toClass();
            dynamicClass.defrost();
        }
        IOException e = assertThrows(IOException.class, () -> DataAccessFactory.getInstance(tempFile.toAbsolutePath().toString()));
        assertEquals("Unsupported file type", e.getMessage());
    }

    private CtClass writeNonImplementedInterfaceClass(ClassPool pool, String magicNumber) throws CannotCompileException, IOException, NotFoundException {
        CtClass dynamicClass = pool.makeClass("ch.supsi.dataaccess.image.NonImplementedInterfaceClass");

        addConstructorToDynClass(dynamicClass);

        CtMethod getInstanceMethod = CtMethod.make(
                "public static NonImplementedInterfaceClass getInstance(){ return new NonImplementedInterfaceClass(); }",
                dynamicClass
        );
        dynamicClass.addMethod(getInstanceMethod);
        addAnnotationToDynClass(dynamicClass, magicNumber);

        String outputDirectory = "target/classes";
        dynamicClass.writeFile(outputDirectory);
        dynamicClass.defrost();
        return dynamicClass;
    }

}

