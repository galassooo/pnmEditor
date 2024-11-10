package ch.supsi.business.filters;

import ch.supsi.business.filter.FilterFactory;
import ch.supsi.business.filter.chain.FilterCommand;
import javassist.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

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
        pool.importPackage("ch.supsi.business.filters.strategy");


        try { //safety check
            Class.forName("ch.supsi.business.filters.strategy.AbstractFilter");
            System.out.println("Dynamic test class found in VM");

        } catch (ClassNotFoundException e) {
            System.out.println("Dynamic test class not found in VM... Creating an instance...");

            CtClass dynamicClass = pool.makeClass("ch.supsi.business.filters.strategy.AbstractFilter");
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
        pool.importPackage("ch.supsi.business.filters.strategy");


        try { //safety check
            Class.forName("ch.supsi.business.filters.strategy.NoNoArgConstructor");
            System.out.println("Dynamic test class found in VM");

        } catch (ClassNotFoundException e) {
            System.out.println("Dynamic test class not found in VM... Creating an instance...");

            CtClass dynamicClass = pool.makeClass("ch.supsi.business.filters.strategy.NoNoArgConstructor");
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
        pool.importPackage("ch.supsi.business.filters.strategy");
        pool.importPackage("import java.io.IOException;");


        try { //safety check
            Class.forName("ch.supsi.business.filters.strategy.exceptionConstructor");
            System.out.println("Dynamic test class found in VM");

        } catch (ClassNotFoundException e) {
            System.out.println("Dynamic test class not found in VM... Creating an instance...");

            CtClass dynamicClass = pool.makeClass("ch.supsi.business.filters.strategy.exceptionConstructor");
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
}
