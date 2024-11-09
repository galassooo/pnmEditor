package ch.supsi.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import java.util.List;

public class ImageAccessPluginTest {
    // Inner classes rimangono invariate
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
    }

    static class SimpleFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
        private List<SimpleClassFile> compiled = new ArrayList<>();

        protected SimpleFileManager(StandardJavaFileManager fileManager) {
            super(fileManager);
        }

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

    // Helper method per la compilazione
    private byte[] compile(String qualifiedClassName, String testSource) {
        StringWriter output = new StringWriter();
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        StandardJavaFileManager standardFileManager = compiler.getStandardFileManager(diagnostics, null, null);
        SimpleFileManager fileManager = new SimpleFileManager(standardFileManager);

        String dataAccessComponentSource = """
            package ch.supsi;
            
            public class DataAccessComponent {
                public String[] magicNumber;
                public String extension;
                public Class<?> clazz;
            }
            """;

        String imageAccessSource = """
            package ch.supsi;
            
            import java.lang.annotation.ElementType;
            import java.lang.annotation.Retention;
            import java.lang.annotation.RetentionPolicy;
            import java.lang.annotation.Target;
            
            @Retention(RetentionPolicy.SOURCE)
            @Target(ElementType.TYPE)
            public @interface ImageAccess {
                String[] magicNumber();
                String extension() default "";
            }
            """;

        String imageDataAccessSource = """
            package ch.supsi.business.image;
            
            public interface ImageDataAccess {
                // Interface methods can be empty for testing
            }
            """;

        String factorySource = """
            package ch.supsi;
            
            import java.util.List;
            import java.util.ArrayList;
            
            public class DataAccessFactory {
                protected static List<DataAccessComponent> dataAccesses;
            }
            """;

        List<JavaFileObject> compilationUnits = new ArrayList<>();
        compilationUnits.add(new SimpleSourceFile("ch.supsi.DataAccessComponent", dataAccessComponentSource));
        compilationUnits.add(new SimpleSourceFile("ch.supsi.annotation.ImageAccess", imageAccessSource));
        compilationUnits.add(new SimpleSourceFile("ch.supsi.business.image.ImageDataAccess", imageDataAccessSource));
        compilationUnits.add(new SimpleSourceFile("ch.supsi.DataAccessFactory", factorySource));
        compilationUnits.add(new SimpleSourceFile(qualifiedClassName, testSource));

        List<String> arguments = new ArrayList<>();
        arguments.addAll(asList(
                "-classpath", System.getProperty("java.class.path"),
                "-Xplugin:ImageAccessPlugin",
                "-proc:none"
        ));

        JavaCompiler.CompilationTask task = compiler.getTask(
                output,
                fileManager,
                diagnostics,
                arguments,
                null,
                compilationUnits
        );

        boolean success = task.call();

        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
            System.out.println(diagnostic.getMessage(null));
        }

        if (!success) {
            System.err.println("Compilation output: " + output);
            throw new RuntimeException("Compilation failed: " + output);
        }

        return fileManager.getCompiled().get(fileManager.getCompiled().size() - 1).getCompiledBinaries();
    }

    @Test
    void testValidDataAccess() {
        String source = """
        package ch.supsi;  // Cambiato il package qui
        import ch.supsi.annotation.ImageAccess;
        import ch.supsi.business.image.ImageDataAccess;
        
        @ImageAccess(magicNumber = {"89", "50"}, extension = "png")
        public class PngDataAccess implements ImageDataAccess {
            private static PngDataAccess instance;
            private PngDataAccess() {}
            
            public static ImageDataAccess getInstance() {
                if (instance == null) {
                    instance = new PngDataAccess();
                }
                return instance;
            }
        }
        """;

        byte[] compiled = compile("ch.supsi.PngDataAccess", source);  // Cambiato il qualifiedClassName
        assertNotNull(compiled);
        assertTrue(compiled.length > 0, "Compiled bytecode should not be empty");
    }

    @Test
    void testInvalidDataAccess() {
        String source = """
        package ch.supsi;  // Cambiato il package qui
        import ch.supsi.annotation.ImageAccess;
        import ch.supsi.business.image.ImageDataAccess;
        
        @ImageAccess(magicNumber = {"89", "50"})
        public class PngReader implements ImageDataAccess {
            private static PngReader instance;
            
            public static PngReader getInstance() {
                if (instance == null) {
                    instance = new PngReader();
                }
                return instance;
            }
        }
        """;

        assertThrows(RuntimeException.class, () -> {
            compile("ch.supsi.PngReader", source);  // Cambiato il qualifiedClassName
        }, "Class should fail validation due to invalid name pattern");
    }

    @Test
    void testNonStaticGetInstance() {
        String source = """
        package ch.supsi;  // Cambiato il package qui
        import ch.supsi.annotation.ImageAccess;
        import ch.supsi.business.image.ImageDataAccess;
        
        @ImageAccess(magicNumber = {"89", "50"}, extension = "png")
        public class PngDataAccess implements ImageDataAccess {
            private PngDataAccess instance;
            
            public ImageDataAccess getInstance() {  // non-static getInstance
                if (instance == null) {
                    instance = new PngDataAccess();
                }
                return instance;
            }
        }
        """;

        assertThrows(RuntimeException.class, () -> {
            compile("ch.supsi.PngDataAccess", source);  // Cambiato il qualifiedClassName
        }, "Class should fail validation due to non-static getInstance");
    }
}