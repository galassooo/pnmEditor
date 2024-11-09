package ch.supsi.test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import static java.util.Arrays.asList;
import java.util.List;

public class ImageAccessPluginTest {

    //source file holder
    static class SimpleSourceFile extends SimpleJavaFileObject {
        private final String content;

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

    //class file holder
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

    //file manager
    static class SimpleFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
        private final List<SimpleClassFile> compiled = new ArrayList<>();

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
    private byte[] compile(String qualifiedClassName, String testSource, boolean factoryRequired) {
        StringWriter output = new StringWriter();
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        StandardJavaFileManager standardFileManager = compiler.getStandardFileManager(diagnostics, null, null);
        SimpleFileManager fileManager = new SimpleFileManager(standardFileManager);

        // support class definition (interfaces, annotations,  etc)
        String dataAccessComponentSource = """
            package ch.supsi;
            
            public class DataAccessComponent {
                public String[] magicNumber;
                public String extension;
                public Class<?> clazz;
            }
            """;

        String imageAccessSource = """
            package ch.supsi.annotation;
            
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

        String imageAccessFactorySource = """
            package ch.supsi.annotation;
            
            import java.lang.annotation.ElementType;
            import java.lang.annotation.Retention;
            import java.lang.annotation.RetentionPolicy;
            import java.lang.annotation.Target;
            
            @Retention(RetentionPolicy.SOURCE)
            @Target(ElementType.TYPE)
            public @interface ImageAccessFactory {}
            """;

        String registerSource = """
            package ch.supsi.annotation;
            
            import java.lang.annotation.ElementType;
            import java.lang.annotation.Retention;
            import java.lang.annotation.RetentionPolicy;
            import java.lang.annotation.Target;
            
            @Retention(RetentionPolicy.SOURCE)
            @Target(ElementType.FIELD)
            public @interface Register {}
            """;

        String imageDataAccessSource = """
            package ch.supsi.business.image;
            
            public interface ImageDataAccess {
                // Interface methods can be empty for testing
            }
            """;

        String factorySource = """
            package ch.supsi;
            
            import ch.supsi.annotation.ImageAccessFactory;
            import ch.supsi.annotation.Register;
            import java.util.List;
            import java.util.ArrayList;
            
            @ImageAccessFactory
            public class MyFactory {
                @Register
                protected static List<DataAccessComponent> dataAccesses;
            }
            """;

        List<JavaFileObject> compilationUnits = new ArrayList<>();
        compilationUnits.add(new SimpleSourceFile("ch.supsi.DataAccessComponent", dataAccessComponentSource));
        compilationUnits.add(new SimpleSourceFile("ch.supsi.annotation.ImageAccess", imageAccessSource));
        compilationUnits.add(new SimpleSourceFile("ch.supsi.annotation.ImageAccessFactory", imageAccessFactorySource));
        compilationUnits.add(new SimpleSourceFile("ch.supsi.annotation.Register", registerSource));
        compilationUnits.add(new SimpleSourceFile("ch.supsi.business.image.ImageDataAccess", imageDataAccessSource));
        if(factoryRequired) {
            compilationUnits.add(new SimpleSourceFile("ch.supsi.MyFactory", factorySource));
        }
        compilationUnits.add(new SimpleSourceFile(qualifiedClassName, testSource));

        List<String> arguments = new ArrayList<>(asList(
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
        package ch.supsi;
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

        byte[] compiled = compile("ch.supsi.PngDataAccess", source, true);
        assertNotNull(compiled);
        assertTrue(compiled.length > 0);
    }

    @Test
    void testValidDataAccessWithoutExplicitExtension() {
        String source = """
        package ch.supsi;
        import ch.supsi.annotation.ImageAccess;
        import ch.supsi.business.image.ImageDataAccess;
        
        @ImageAccess(magicNumber = {"89", "50"})
        public class JpegDataAccess implements ImageDataAccess {
            private static JpegDataAccess instance;
            private JpegDataAccess() {}

            public static ImageDataAccess getInstance() {
                if (instance == null) {
                    instance = new JpegDataAccess();
                }
                return instance;
            }
        }
        """;

        byte[] compiled = compile("ch.supsi.JpegDataAccess", source, true);
        assertNotNull(compiled);
        assertTrue(compiled.length > 0);
    }

    @Test
    void testMultipleValidDataAccesses() {
        String pngSource = """
        package ch.supsi;
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

        String jpegSource = """
        package ch.supsi;
        import ch.supsi.annotation.ImageAccess;
        import ch.supsi.business.image.ImageDataAccess;
        
        @ImageAccess(magicNumber = {"FF", "D8"}, extension = "jpeg")
        public class JpegDataAccess implements ImageDataAccess {
            private static JpegDataAccess instance;
            private JpegDataAccess() {}

            public static ImageDataAccess getInstance() {
                if (instance == null) {
                    instance = new JpegDataAccess();
                }
                return instance;
            }
        }
        """;

        // Dovrebbe compilare con successo entrambe le classi
        byte[] compiled1 = compile("ch.supsi.PngDataAccess", pngSource, true);
        byte[] compiled2 = compile("ch.supsi.JpegDataAccess", jpegSource, true);
        assertNotNull(compiled1);
        assertNotNull(compiled2);
    }

    @Test
    void testInvalidClassNameNoExtension() {
        String source = """
        package ch.supsi;
        import ch.supsi.annotation.ImageAccess;
        import ch.supsi.business.image.ImageDataAccess;
        
        @ImageAccess(magicNumber = {"89", "50"})
        public class PngReader implements ImageDataAccess {
            private static PngReader instance;

            public static ImageDataAccess getInstance() {
                if (instance == null) {
                    instance = new PngReader();
                }
                return instance;
            }
        }
        """;

        assertThrows(RuntimeException.class, () -> {
            compile("ch.supsi.PngReader", source, true);
        }, "Class should fail validation due to invalid name pattern and no explicit extension");
    }

    @Test
    void testAmbiguousExtension() {
        String source = """
        package ch.supsi;
        import ch.supsi.annotation.ImageAccess;
        import ch.supsi.business.image.ImageDataAccess;
        
        @ImageAccess(magicNumber = {"89", "50"}, extension = "jpg")
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

        // Dovrebbe compilare ma generare un warning per l'ambiguità
        byte[] compiled = compile("ch.supsi.PngDataAccess", source, true);
        assertNotNull(compiled);
    }

    @Test
    void testNonStaticGetInstance() {
        String source = """
        package ch.supsi;
        import ch.supsi.annotation.ImageAccess;
        import ch.supsi.business.image.ImageDataAccess;
        
        @ImageAccess(magicNumber = {"89", "50"}, extension = "png")
        public class PngDataAccess implements ImageDataAccess {
            private PngDataAccess instance;

            public ImageDataAccess getInstance() {
                if (instance == null) {
                    instance = new PngDataAccess();
                }
                return instance;
            }
        }
        """;

        assertThrows(RuntimeException.class, () -> {
            compile("ch.supsi.PngDataAccess", source, true);
        }, "Should fail due to non-static getInstance");
    }

    @Test
    void testWrongGetInstanceReturnType() {
        String source = """
        package ch.supsi;
        import ch.supsi.annotation.ImageAccess;
        import ch.supsi.business.image.ImageDataAccess;
        
        @ImageAccess(magicNumber = {"89", "50"}, extension = "png")
        public class PngDataAccess implements ImageDataAccess {
            private static PngDataAccess instance;

            public static String getInstance() {
                return "";
            }
        }
        """;

        assertThrows(RuntimeException.class, () -> {
            compile("ch.supsi.PngDataAccess", source, true);
        }, "Should fail due to wrong getInstance return type");
    }

    @Test
    void testMissingGetInstance() {
        String source = """
        package ch.supsi;
        import ch.supsi.annotation.ImageAccess;
        import ch.supsi.business.image.ImageDataAccess;
        
        @ImageAccess(magicNumber = {"89", "50"}, extension = "png")
        public class PngDataAccess implements ImageDataAccess {
            private static PngDataAccess instance;
        }
        """;

        assertThrows(RuntimeException.class, () -> {
            compile("ch.supsi.PngDataAccess", source, true);
        }, "Should fail due to missing getInstance method");
    }

    @Test
    void testMultipleFactories() {
        String factory2Source = """
        package ch.supsi;
        import ch.supsi.annotation.ImageAccessFactory;
        import ch.supsi.annotation.Register;
        import java.util.List;
        
        @ImageAccessFactory
        public class SecondFactory {
            @Register
            protected static List<DataAccessComponent> components;
        }
        """;

        assertThrows(RuntimeException.class, () -> {
            compile("ch.supsi.SecondFactory", factory2Source, true);
        }, "Multiple ImageAccessFactory classes should not be allowed");
    }

    @Test
    void testMissingRegisterAnnotation() {
        String source = """
        package ch.supsi;
        import ch.supsi.annotation.ImageAccessFactory;
        import java.util.List;
        
        @ImageAccessFactory
        public class InvalidFactory {
            protected static List<DataAccessComponent> components;
        }
        """;

        assertDoesNotThrow(() -> {
            compile("ch.supsi.InvalidFactory", source, false);
        }, "Factory class should not be forced to have a field annotated with @Register");
    }

    @Test
    void testNonStaticRegisterField() {
        String source = """
        package ch.supsi;
        import ch.supsi.annotation.ImageAccessFactory;
        import ch.supsi.annotation.Register;
        import java.util.List;
        
        @ImageAccessFactory
        public class InvalidFactory {
            @Register
            protected List<DataAccessComponent> components;
        }
        """;

        assertThrows(RuntimeException.class, () -> {
            compile("ch.supsi.InvalidFactory", source, false);
        }, "Register field must be static");
    }

    @Test
    void testWrongRegisterFieldType() {
        String source = """
        package ch.supsi;
        import ch.supsi.annotation.ImageAccessFactory;
        import ch.supsi.annotation.Register;
        import java.util.ArrayList;
        
        @ImageAccessFactory
        public class InvalidFactory {
            @Register
            protected static ArrayList<DataAccessComponent> components;
        }
        """;

        assertThrows(RuntimeException.class, () -> {
            compile("ch.supsi.InvalidFactory", source, false);
        }, "Register field must be of type List<DataAccessComponent>");
    }

    @Test
    void testWrongRegisterFieldGenericType() {
        String source = """
        package ch.supsi;
        import ch.supsi.annotation.ImageAccessFactory;
        import ch.supsi.annotation.Register;
        import java.util.List;
        
        @ImageAccessFactory
        public class InvalidFactory {
            @Register
            protected static List<String> components;
        }
        """;

        assertThrows(RuntimeException.class, () -> {
            compile("ch.supsi.InvalidFactory", source, false);
        }, "Register field must be of type List<DataAccessComponent>");
    }


    @Test
    void testNoImageDataAccessImplementation() {
        String source = """
        package ch.supsi;
        import ch.supsi.annotation.ImageAccess;
        
        @ImageAccess(magicNumber = {"89", "50"}, extension = "png")
        public class PngDataAccess {
            private static PngDataAccess instance;

            public static PngDataAccess getInstance() {
                if (instance == null) {
                    instance = new PngDataAccess();
                }
                return instance;
            }
        }
        """;

        assertThrows(RuntimeException.class, () -> {
            compile("ch.supsi.PngDataAccess", source, true);
        }, "Should fail due to missing ImageDataAccess implementation");
    }

    @Test
    void testDataAccessWithEmptyExtension() {
        String source = """
        package ch.supsi;
        import ch.supsi.annotation.ImageAccess;
        import ch.supsi.business.image.ImageDataAccess;
        
        @ImageAccess(magicNumber = {"89", "50"}, extension = "")
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

        assertDoesNotThrow(() -> {
            compile("ch.supsi.PngDataAccess", source, true);
        }, "Should not fail due to empty extension (XXXDataAccess)");
    }

    @Test
    void testRegisterFieldWithRawList() {
        String source = """
        package ch.supsi;
        import ch.supsi.annotation.ImageAccessFactory;
        import ch.supsi.annotation.Register;
        import java.util.List;
        
        @ImageAccessFactory
        public class InvalidFactory {
            @Register
            protected static List components;  // Raw type List
        }
        """;

        assertThrows(RuntimeException.class, () -> {
            compile("ch.supsi.InvalidFactory", source, false);
        }, "Should fail due to raw type List");
    }

    @Test
    void testRegisterFieldWithWildcardGenerics() {
        String source = """
        package ch.supsi;
        import ch.supsi.annotation.ImageAccessFactory;
        import ch.supsi.annotation.Register;
        import java.util.List;
        
        @ImageAccessFactory
        public class InvalidFactory {
            @Register
            protected static List<? extends DataAccessComponent> components;
        }
        """;

        assertThrows(RuntimeException.class, () -> {
            compile("ch.supsi.InvalidFactory", source, false);
        }, "Should fail due to wildcard generics");
    }

    @Test
    void testDataAccessWithNonPrivateConstructor() {
        String source = """
        package ch.supsi;
        import ch.supsi.annotation.ImageAccess;
        import ch.supsi.business.image.ImageDataAccess;
        
        @ImageAccess(magicNumber = {"89", "50"}, extension = "png")
        public class PngDataAccess implements ImageDataAccess {
            private static PngDataAccess instance;

            public PngDataAccess() {}  // Public constructor violates singleton

            public static ImageDataAccess getInstance() {
                if (instance == null) {
                    instance = new PngDataAccess();
                }
                return instance;
            }
        }
        """;

        assertDoesNotThrow(() -> {
            compile("ch.supsi.PngDataAccess", source, false);
        }, "Should not fail due to non-private constructor");
    }


    @Test
    void testFactoryWithFinalRegisterField() {
        String source = """
        package ch.supsi;
        import ch.supsi.annotation.ImageAccessFactory;
        import ch.supsi.annotation.Register;
        import java.util.List;
        
        @ImageAccessFactory
        public class InvalidFactory {
            @Register
            protected static final List<DataAccessComponent> components = null;
        }
        """;

        assertThrows(RuntimeException.class, () -> {
            compile("ch.supsi.InvalidFactory", source, false);
        }, "Should fail due to final register field");
    }

    @Test
    void testCompileSimpleClass() {
        String source = """
        package ch.supsi;
        
        public class SimpleClass {
            private String simpleField;
            private int simpleField1;

            public void simpleMethod() {
                simpleField1 = 1;
            }
        }
        """;

        assertDoesNotThrow(() -> {
            compile("ch.supsi.SimpleClass", source, true);
        }, "compilation should not fail");
    }
}