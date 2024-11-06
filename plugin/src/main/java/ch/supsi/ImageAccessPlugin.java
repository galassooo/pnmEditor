package ch.supsi;

import com.sun.source.tree.*;
import com.sun.source.util.*;
import com.sun.tools.javac.api.BasicJavacTask;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;

public class ImageAccessPlugin implements Plugin {
    private final Set<String> processedClasses = new HashSet<>();
    private final ArrayList<ClassToValidate> classesToValidate = new ArrayList<>();
    private final ArrayList<AnnotatedClassInfo> validatedClasses = new ArrayList<>();
    private JCTree.JCClassDecl factoryClassDecl = null;
    private TreeMaker maker = null;
    private Names names = null;
    private boolean codeGenerated = false;
    private JavacTask javacTask = null;

    private static class ClassToValidate {
        final ClassTree classTree;
        final TreePath path;
        final String[] magicNumbers;
        final String declaredExtension;

        ClassToValidate(ClassTree classTree, TreePath path, String[] magicNumbers, String declaredExtension) {
            this.classTree = classTree;
            this.path = path;
            this.magicNumbers = magicNumbers;
            this.declaredExtension = declaredExtension;
        }
    }

    private static class AnnotatedClassInfo {
        final String className;
        final String extension;
        final String[] magicNumbers;

        AnnotatedClassInfo(String className, String extension, String[] magicNumbers) {
            this.className = className;
            this.extension = extension;
            this.magicNumbers = magicNumbers;
        }
    }

    @Override
    public String getName() {
        return "ImageAccessPlugin";
    }

    @Override
    public void init(JavacTask task, String... args) {
        Context context = ((BasicJavacTask) task).getContext();
        this.maker = TreeMaker.instance(context);
        this.names = Names.instance(context);
        this.javacTask = task;

        task.addTaskListener(new TaskListener() {
            @Override
            public void finished(TaskEvent e) {
                if (e.getKind() == TaskEvent.Kind.PARSE) {
                    CompilationUnitTree cu = e.getCompilationUnit();
                    Trees trees = Trees.instance(task);

                    if (factoryClassDecl == null) {
                        new TreePathScanner<Void, Trees>() {
                            @Override
                            public Void visitClass(ClassTree node, Trees trees) {
                                if (node.getSimpleName().toString().equals("DataAccessFactory")) {
                                    factoryClassDecl = (JCTree.JCClassDecl) node;
                                }
                                return super.visitClass(node, trees);
                            }
                        }.scan(cu, trees);
                    }

                    new TreePathScanner<Void, Trees>() {
                        @Override
                        public Void visitClass(ClassTree node, Trees trees) {
                            if (hasImageAccessAnnotation(node)) {
                                String className = node.getSimpleName().toString();
                                if (!processedClasses.contains(className)) {
                                    processedClasses.add(className);
                                    collectClassForValidation(node, getCurrentPath());
                                }
                            }
                            return super.visitClass(node, trees);
                        }
                    }.scan(cu, trees);
                }
                else if (e.getKind() == TaskEvent.Kind.ENTER && !codeGenerated && factoryClassDecl != null) {
                    validateCollectedClasses();
                    injectStaticInitializer();
                    codeGenerated = true;
                }
            }
        });
    }

    private void collectClassForValidation(ClassTree classTree, TreePath path) {
        for (AnnotationTree annotation : classTree.getModifiers().getAnnotations()) {
            if (annotation.getAnnotationType().toString().endsWith("ImageAccess")) {
                String[] magicNumbers = null;
                String declaredExtension = null;

                for (ExpressionTree arg : annotation.getArguments()) {
                    String argStr = arg.toString();
                    if (argStr.startsWith("magicNumber")) {
                        magicNumbers = extractMagicNumbers(annotation);
                    } else if (argStr.startsWith("extension")) {
                        declaredExtension = extractExtension(arg);
                    }
                }

                classesToValidate.add(new ClassToValidate(classTree, path, magicNumbers, declaredExtension));
                break;
            }
        }
    }

    private String extractExtension(ExpressionTree arg) {
        return arg.toString()
                .replace("extension = ", "")
                .replace("\"", "")
                .trim();
    }

    private String validateAndGetExtension(String className, String declaredExtension) {
        String conventionalExtension = null;
        if (className.endsWith("DataAccess")) {
            conventionalExtension = className
                    .substring(0, className.length() - "DataAccess".length())
                    .toLowerCase();
        }

        if (declaredExtension != null && !declaredExtension.isEmpty()) {
            if (conventionalExtension != null && !conventionalExtension.equals(declaredExtension)) {
                System.out.println("\nWARNING: Ambiguous extension declaration in " + className +
                        ".\n  Class name suggests '" + conventionalExtension +
                        "' but annotation declares '" + declaredExtension +
                        "'.\n  Using declared extension '" + declaredExtension + "'");
            }
            return declaredExtension;
        } else {
            if (conventionalExtension != null) {
                return conventionalExtension;
            } else {
                throw new RuntimeException("Compilation error: Class " + className +
                        " does not follow XXXDataAccess naming convention and no extension is declared in @ImageAccess annotation");
            }
        }
    }

    private void validateCollectedClasses() {
        Trees trees = Trees.instance(javacTask);
        Elements elements = javacTask.getElements();
        Types types = javacTask.getTypes();

        TypeElement imageDataAccessElement = elements.getTypeElement("ch.supsi.business.image.ImageDataAccess");
        if (imageDataAccessElement == null) {
            System.out.println("WARNING: Could not find ImageDataAccess interface");
            return;
        }

        for (ClassToValidate classToValidate : classesToValidate) {
            ClassTree classTree = classToValidate.classTree;
            String className = classTree.getSimpleName().toString();
            boolean isValid = true;
            String validationErrors = "";

            boolean hasValidGetInstance = false;
            for (Tree member : classTree.getMembers()) {
                if (member instanceof MethodTree) {
                    MethodTree method = (MethodTree) member;
                    if (method.getName().toString().equals("getInstance")) {
                        boolean isStatic = method.getModifiers().getFlags()
                                .contains(javax.lang.model.element.Modifier.STATIC);

                        Element methodElement = trees.getElement(new TreePath(classToValidate.path, method));
                        if (methodElement instanceof ExecutableElement) {
                            ExecutableElement executableElement = (ExecutableElement) methodElement;
                            TypeMirror returnType = executableElement.getReturnType();

                            boolean hasValidReturnType = types.isAssignable(returnType, imageDataAccessElement.asType());

                            if (!isStatic) {
                                isValid = false;
                                validationErrors += "\nWARNING: getInstance in " + className + " must be static";
                            }
                            if (!hasValidReturnType) {
                                isValid = false;
                                validationErrors += "\nWARNING: getInstance in " + className +
                                        " must return a type assignable to ImageDataAccess";
                            }

                            hasValidGetInstance = isStatic && hasValidReturnType;
                        }
                        break;
                    }
                }
            }

            if (!hasValidGetInstance) {
                isValid = false;
                validationErrors += "\nWARNING: " + className + " must provide a valid getInstance method";
            }

            if (!isValid) {
                System.out.println(validationErrors);
                System.out.println("Class " + className + " will not be included in static initialization");
                continue;
            }

            try {
                String extension = validateAndGetExtension(className, classToValidate.declaredExtension);
                validatedClasses.add(new AnnotatedClassInfo(className, extension, classToValidate.magicNumbers));
                System.out.println("Valid class " + className + " with magic numbers: " +
                        Arrays.toString(classToValidate.magicNumbers) + " and extension: " + extension);
            } catch (RuntimeException e) {
                System.err.println(e.getMessage());
                throw e;
            }
        }
    }

    private void injectStaticInitializer() {
        System.out.println("Generating static initializer...");

        List<JCTree.JCStatement> statements = List.nil();

        // Creiamo una lista temporanea con tutti i componenti
        statements = statements.append(
                maker.VarDef(
                        maker.Modifiers(0),
                        names.fromString("tempList"),
                        maker.TypeApply(
                                maker.Ident(names.fromString("List")),
                                List.of(maker.Ident(names.fromString("DataAccessComponent")))
                        ),
                        maker.NewClass(
                                null,
                                List.nil(),
                                maker.Select(
                                        maker.Select(
                                                maker.Ident(names.fromString("java")),
                                                names.fromString("util")
                                        ),
                                        names.fromString("ArrayList")
                                ),
                                List.nil(),
                                null
                        )
                )
        );

        // Usiamo un contatore per creare nomi unici
        int componentCounter = 0;
        for (AnnotatedClassInfo info : validatedClasses) {
            String componentName = "component" + componentCounter++;

            statements = statements.append(
                    maker.VarDef(
                            maker.Modifiers(0),
                            names.fromString(componentName),
                            maker.Ident(names.fromString("DataAccessComponent")),
                            maker.NewClass(
                                    null,
                                    List.nil(),
                                    maker.Ident(names.fromString("DataAccessComponent")),
                                    List.nil(),
                                    null
                            )
                    )
            );

            // Settiamo i magic numbers
            List<JCTree.JCExpression> magicValues = List.nil();
            for (String magic : info.magicNumbers) {
                magicValues = magicValues.append(maker.Literal(magic));
            }
            statements = statements.append(
                    maker.Exec(
                            maker.Assign(
                                    maker.Select(
                                            maker.Ident(names.fromString(componentName)),
                                            names.fromString("magicNumber")
                                    ),
                                    maker.NewArray(
                                            maker.Ident(names.fromString("String")),
                                            List.nil(),
                                            magicValues
                                    )
                            )
                    )
            );

            // Settiamo extension
            statements = statements.append(
                    maker.Exec(
                            maker.Assign(
                                    maker.Select(
                                            maker.Ident(names.fromString(componentName)),
                                            names.fromString("extension")
                                    ),
                                    maker.Literal(info.extension)
                            )
                    )
            );

            // Settiamo class
            statements = statements.append(
                    maker.Exec(
                            maker.Assign(
                                    maker.Select(
                                            maker.Ident(names.fromString(componentName)),
                                            names.fromString("clazz")
                                    ),
                                    maker.Select(
                                            maker.Ident(names.fromString(info.className)),
                                            names.fromString("class")
                                    )
                            )
                    )
            );

            // Aggiungiamo alla lista temporanea
            statements = statements.append(
                    maker.Exec(
                            maker.Apply(
                                    List.nil(),
                                    maker.Select(
                                            maker.Ident(names.fromString("tempList")),
                                            names.fromString("add")
                                    ),
                                    List.of(maker.Ident(names.fromString(componentName)))
                            )
                    )
            );
        }

        // Assegnamo la lista temporanea a dataAccesses
        statements = statements.append(
                maker.Exec(
                        maker.Assign(
                                maker.Ident(names.fromString("dataAccesses")),
                                maker.Ident(names.fromString("tempList"))
                        )
                )
        );

        JCTree.JCBlock staticBlock = maker.Block(
                Flags.STATIC,
                statements
        );

        factoryClassDecl.defs = factoryClassDecl.defs.append(staticBlock);

        System.out.println("Static initializer generated successfully");
    }


    private String[] extractMagicNumbers(AnnotationTree annotation) {
        return annotation.getArguments().stream()
                .filter(arg -> arg.toString().startsWith("magicNumber"))
                .flatMap(arg -> Arrays.stream(arg.toString()
                                .replace("magicNumber = ", "")
                                .replace("{", "")
                                .replace("}", "")
                                .split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty()))
                .toArray(String[]::new);
    }

    private boolean hasImageAccessAnnotation(ClassTree classTree) {
        return classTree.getModifiers().getAnnotations().stream()
                .anyMatch(a -> a.getAnnotationType().toString().endsWith("ImageAccess"));
    }
}
