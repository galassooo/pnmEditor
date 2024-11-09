package ch.supsi.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;

import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ImageAccessPluginTest {

    static class SimpleSourceFile extends SimpleJavaFileObject {
        private String content;

        public SimpleSourceFile(String qualifiedClassName, String testSource) {
            super(URI.create(String.format(
                    "file://%s%s", qualifiedClassName.replaceAll("\\.", "/"),
                    Kind.SOURCE.extension)), Kind.SOURCE);
            content = testSource;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return content;
        }
    }

    static class SimpleClassFile extends SimpleJavaFileObject {

        private ByteArrayOutputStream out;

        public SimpleClassFile(URI uri) {
            super(uri, Kind.CLASS);
        }

        @Override
        public OutputStream openOutputStream() throws IOException {
            return out = new ByteArrayOutputStream();
        }

        public byte[] getCompiledBinaries() {
            return out.toByteArray();
        }

        // getters
    }

    static class SimpleFileManager
            extends ForwardingJavaFileManager<StandardJavaFileManager> {

        private List<SimpleClassFile> compiled = new ArrayList<>();

        /**
         * Creates a new instance of {@code ForwardingJavaFileManager}.
         *
         * @param fileManager delegate to this file manager
         */
        protected SimpleFileManager(StandardJavaFileManager fileManager) {
            super(fileManager);
        }

        // standard constructors/getters

        @Override
        public JavaFileObject getJavaFileForOutput(Location location,
                                                   String className, JavaFileObject.Kind kind, FileObject sibling) {
            SimpleClassFile result = new SimpleClassFile(
                    URI.create("string://" + className));
            compiled.add(result);
            return result;
        }

        public List<SimpleClassFile> getCompiled() {
            return compiled;
        }
    }

    public byte[] compile(String qualifiedClassName, String testSource) {
        StringWriter output = new StringWriter();

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        SimpleFileManager fileManager = new SimpleFileManager(
                compiler.getStandardFileManager(null, null, null));
        List<SimpleSourceFile> compilationUnits
                = singletonList(new SimpleSourceFile(qualifiedClassName, testSource));
        List<String> arguments = new ArrayList<>();
        arguments.addAll(asList("-classpath", System.getProperty("java.class.path"),
                "-Xplugin:ImageAccessPlugin" ));
        JavaCompiler.CompilationTask task
                = compiler.getTask(output, fileManager, null, arguments, null,
                compilationUnits);

        task.call();
        return fileManager.getCompiled().iterator().next().getCompiledBinaries();

    }

    @Test
    void testValidDataAccess() {
        String source = """
        package ch.supsi.test;
        import ch.supsi.ImageAccess;
        import ch.supsi.business.image.ImageDataAccess;
        
        @ImageAccess(magicNumber = {"89", "50"})
        public class PngDataAccess implements ImageDataAccess {
            private static PngDataAccess instance;
            public static ImageDataAccess getInstance() {
                if (instance == null) {
                    instance = new PngDataAccess();
                }
                return instance;
            }
        }
        """;

        try {
            byte[] compiled = compile("ch.supsi.test.PngDataAccess", source);
            // Se arriva qui, la compilazione è avvenuta con successo
            fail("Compilation should have failed due to missing ImageDataAccess interface");
        } catch (Exception e) {
            // Ci aspettiamo che fallisca per l'interfaccia mancante
            assertTrue(true);
        }
    }

    @Test
    void testInvalidDataAccess() {
        String source = """
        package ch.supsi.test;
        import ch.supsi.ImageAccess;
        
        @ImageAccess(magicNumber = {"89", "50"})
        public class PngReader {
            private static PngReader instance;
            public static PngReader getInstance() {
                if (instance == null) {
                    instance = new PngReader();
                }
                return instance;
            }
        }
        """;

        try {
            byte[] compiled = compile("ch.supsi.test.PngReader", source);
            // Se arriva qui, la compilazione è avvenuta con successo
            fail("Compilation should have failed due to invalid class name");
        } catch (Exception e) {
            // Ci aspettiamo che fallisca per il nome della classe non valido
            assertTrue(true);
        }
    }


}