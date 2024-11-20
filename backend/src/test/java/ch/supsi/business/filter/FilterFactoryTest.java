package ch.supsi.business.filter;

import ch.supsi.business.filter.chain.command.FilterCommand;
import javassist.*;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class FilterFactoryTest {

    /* ----------- valid data ----------- */

    @Test
    void test(){

    }

    /* ----------- invalid data ----------- */

    @Test
    void testAbstractClass() throws NotFoundException, CannotCompileException, IOException { //per i commenti dettagliati guarda l'altra Factory

        ClassPool pool = ClassPool.getDefault();
        pool.importPackage("ch.supsi.business.filters");
        pool.importPackage("ch.supsi.business.filter.chain.command");


        try { //safety check
            Class.forName("ch.supsi.business.filter.chain.command.AbstractFilter");
            System.out.println("Dynamic test class found in VM");

        } catch (ClassNotFoundException e) {
            System.out.println("Dynamic test class not found in VM... Creating an instance...");

            CtClass dynamicClass = pool.makeClass("ch.supsi.business.filter.chain.command.AbstractFilter");
            dynamicClass.addInterface(pool.get(FilterCommand.class.getName()));
            dynamicClass.setModifiers(Modifier.ABSTRACT);

            String outputDirectory = "target/classes";
            dynamicClass.writeFile(outputDirectory);

            dynamicClass.defrost();
            Class<?> clazz = dynamicClass.toClass();
            dynamicClass.defrost();
        }
        FilterFactory.reload();
        assertDoesNotThrow(FilterFactory::getFilters);
    }

    @Test
    void testNoNoArgConstructor() throws NotFoundException, CannotCompileException, IOException { //per i commenti dettagliati guarda l'altra Factory

        ClassPool pool = ClassPool.getDefault();
        pool.importPackage("ch.supsi.business.filters");
        pool.importPackage("ch.supsi.business.filter.chain.command");


        try { //safety check
            Class.forName("ch.supsi.business.filter.chain.command.NoNoArgConstructor");
            System.out.println("Dynamic test class found in VM");

        } catch (ClassNotFoundException e) {
            System.out.println("Dynamic test class not found in VM... Creating an instance...");

            CtClass dynamicClass = pool.makeClass("ch.supsi.business.filter.chain.command.NoNoArgConstructor");
            dynamicClass.addInterface(pool.get(FilterCommand.class.getName()));

            CtClass[] paramTypes = new CtClass[]{pool.get("java.lang.String")};

            CtConstructor constructor = new CtConstructor(paramTypes, dynamicClass);
            constructor.setBody("{ System.out.println(\"Costruttore con argomento: \" + $1); }");

            dynamicClass.addConstructor(constructor);


            String outputDirectory = "target/classes";
            dynamicClass.writeFile(outputDirectory);

            dynamicClass.defrost();
            Class<?> clazz = dynamicClass.toClass();
            dynamicClass.defrost();
        }
        FilterFactory.reload();
        assertDoesNotThrow(FilterFactory::getFilters);
    }
    @Test
    void exceptionConstructor() throws NotFoundException, CannotCompileException, IOException { //per i commenti dettagliati guarda l'altra Factory

        ClassPool pool = ClassPool.getDefault();
        pool.importPackage("ch.supsi.business.filters");
        pool.importPackage("ch.supsi.business.filter.chain.command");
        pool.importPackage("import java.io.IOException;");


        try { //safety check
            Class.forName("ch.supsi.business.filter.chain.command.exceptionConstructor");
            System.out.println("Dynamic test class found in VM");

        } catch (ClassNotFoundException e) {
            System.out.println("Dynamic test class not found in VM... Creating an instance...");

            CtClass dynamicClass = pool.makeClass("ch.supsi.business.filter.chain.command.exceptionConstructor");
            dynamicClass.addInterface(pool.get(FilterCommand.class.getName()));

            CtClass[] paramTypes = new CtClass[]{pool.get("java.lang.String")};

            CtConstructor constructor = new CtConstructor(new CtClass[]{}, dynamicClass);
            constructor.setBody("{ throw new java.io.IOException(\"Errore durante l'istanza della classe.\"); }");
            dynamicClass.addConstructor(constructor);


            String outputDirectory = "target/classes";
            dynamicClass.writeFile(outputDirectory);

            dynamicClass.defrost();
            Class<?> clazz = dynamicClass.toClass();
            dynamicClass.defrost();
        }
        FilterFactory.reload();
        assertDoesNotThrow(FilterFactory::getFilters);
    }

    @Test
    void testNullTransactionGetName() throws NotFoundException, CannotCompileException, IOException {
        ClassPool pool = ClassPool.getDefault();
        pool.importPackage("ch.supsi.business.filters");
        pool.importPackage("ch.supsi.business.filter.chain.command");

        try {
            Class.forName("ch.supsi.business.filter.chain.command.NullNameFilterTrans");
            System.out.println("Dynamic test class found in VM");
        } catch (ClassNotFoundException e) {
            System.out.println("Dynamic test class not found in VM... Creating an instance...");

            CtClass dynamicClass = pool.makeClass("ch.supsi.business.filter.chain.command.NullNameFilterTrans");
            dynamicClass.addInterface(pool.get(FilterCommand.class.getName()));

            // Costruttore base
            CtConstructor constructor = new CtConstructor(new CtClass[]{}, dynamicClass);
            constructor.setBody("{}");
            dynamicClass.addConstructor(constructor);

            // Metodo getName che ritorna null
            dynamicClass.addMethod(CtMethod.make(
                    "public String getName() { return \"ciao\"; }",
                    dynamicClass
            ));

            // Metodo execute vuoto (ma necessario per l'interfaccia)
            dynamicClass.addMethod(CtMethod.make(
                    "public void execute(ch.supsi.application.image.WritableImage image) {}",
                    dynamicClass
            ));

            String outputDirectory = "target/classes";
            dynamicClass.writeFile(outputDirectory);

            dynamicClass.defrost();
            Class<?> clazz = dynamicClass.toClass();
            dynamicClass.defrost();
        }
        FilterFactory.reload();
        assertDoesNotThrow(FilterFactory::getFilters);
    }
    @ParameterizedTest //testa tutti i valori sotto con un unico test
    @MethodSource("filtersName")
    void testColorToGrayscaleConversion(String value) {
        assertNotNull(FilterFactory.getFilter(value));
    }

    private static Stream<Arguments> filtersName() {
        return Stream.of(
                Arguments.of("Negative"),
                Arguments.of("Rotate_Right"),
                Arguments.of("Rotate_Left"),
                Arguments.of("Mirror_Horizontal"),
                Arguments.of("Mirror_Vertical")
        );
    }
}
