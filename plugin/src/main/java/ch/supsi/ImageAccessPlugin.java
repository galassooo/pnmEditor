package ch.supsi;

import com.sun.source.tree.*;
import com.sun.source.util.*;
import com.sun.tools.javac.api.BasicJavacTask;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CompletionException;

public class ImageAccessPlugin implements Plugin {

    // to save classes, processed classes, classes to be validate and validated classes
    private final Set<String> processedClasses = new HashSet<>();
    private final ArrayList<ClassToValidate> classesToValidate = new ArrayList<>();
    private final ArrayList<AnnotatedClassInfo> validatedClasses = new ArrayList<>();

    //standard output colors for info messages
    public static final String RESET = "\u001B[0m";
    public static final String BLUE = "\u001B[34m";
    public static final String YELLOW = "\u001B[33m";
    public static final String RED = "\u001B[31m";

    private TreeMaker maker = null;
    private Names names = null;
    private boolean codeGenerated = false;
    private JavacTask javacTask = null;


    private JCTree.JCVariableDecl registerField = null;
    private JCTree.JCClassDecl factoryClassDecl = null;

    // util static inner classes to store data
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
    // util static inner classes to store data
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

    //NOTA BENISSIMISSIMO::::::: il NOME deve corrispondere al NOME IN -Xplugin:NOMEEEEEEEEE
    @Override
    public String getName() {
        return "ImageAccessPlugin";
    }


    //initialize
    @Override
    public void init(JavacTask task, String... args) {
        System.out.println("INITIALiZEEEEEEEEEEEEEEEEEEEEEEEEEEEED");
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

                    // Cerca la classe factory e il campo register
                    if (factoryClassDecl == null) {
                        new TreePathScanner<Void, Trees>() {
                            @Override
                            public Void visitClass(ClassTree node, Trees trees) {
                                if (hasImageAccessFactoryAnnotation(node)) {
                                    System.out.println("[DEBUG] Found factory class: " + node.getSimpleName());
                                    factoryClassDecl = (JCTree.JCClassDecl) node;
                                    // Cerca il campo con @Register
                                    for (Tree member : node.getMembers()) {
                                        if (member instanceof VariableTree) {
                                            VariableTree field = (VariableTree) member;
                                            if (hasRegisterAnnotation(field)) {
                                                System.out.println("[DEBUG] Found register field: " + field.getName());
                                                // Verifica che il campo sia una List<DataAccessComponent>
                                                if (validateRegisterField(field, trees)) {
                                                    registerField = (JCTree.JCVariableDecl) member;
                                                } else {
                                                    throw new RuntimeException("Register field must be of type List<DataAccessComponent>");
                                                }
                                            }
                                        }
                                    }
                                }
                                return super.visitClass(node, trees);
                            }
                        }.scan(cu, trees);
                    }

                    // Il resto del codice per processare le classi annotate rimane uguale
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
                else if (e.getKind() == TaskEvent.Kind.ENTER && !codeGenerated && factoryClassDecl != null && registerField != null) {
                    validateCollectedClasses();
                    injectStaticInitializer();
                    codeGenerated = true;
                }
            }
        });
    }

    private boolean hasImageAccessFactoryAnnotation(ClassTree classTree) {
        return classTree.getModifiers().getAnnotations().stream()
                .anyMatch(a -> a.getAnnotationType().toString().endsWith("ImageAccessFactory"));
    }

    private boolean hasRegisterAnnotation(VariableTree field) {
        return field.getModifiers().getAnnotations().stream()
                .anyMatch(a -> a.getAnnotationType().toString().endsWith("Register"));
    }

    private boolean validateRegisterField(VariableTree field, Trees trees) {
        // Verifica preliminare del tipo raw dalla sintassi
        String typeString = field.getType().toString();
        if (!typeString.startsWith("List<") || !typeString.endsWith(">")) {
            System.out.println("[DEBUG] Field is not a List: " + typeString);
            return false;
        }

        String genericType = typeString.substring(5, typeString.length() - 1);
        if (!genericType.equals("DataAccessComponent")) {
            System.out.println("[DEBUG] List generic type is not DataAccessComponent: " + genericType);
            return false;
        }

        // Verifica che il campo sia static
        ModifiersTree modifiers = field.getModifiers();
        if (!modifiers.getFlags().contains(javax.lang.model.element.Modifier.STATIC)) {
            System.out.println("[DEBUG] Register field must be static");
            return false;
        }

        System.out.println("[DEBUG] Register field validation successful");
        return true;
    }

    //validate class
    private void collectClassForValidation(ClassTree classTree, TreePath path) {
        System.out.println("[DEBUG] Collecting class for validation: " + classTree.getSimpleName());

        for (AnnotationTree annotation : classTree.getModifiers().getAnnotations()) {
            if (annotation.getAnnotationType().toString().endsWith("ImageAccess")) {
                String[] magicNumbers = null;
                String declaredExtension = null;

                System.out.println("[DEBUG] Processing annotation arguments for: " + classTree.getSimpleName());

                for (ExpressionTree arg : annotation.getArguments()) {
                    String argStr = arg.toString();
                    System.out.println("[DEBUG] Processing argument: " + argStr);

                    if (argStr.startsWith("magicNumber")) {
                        magicNumbers = extractMagicNumbers(annotation);
                        System.out.println("[DEBUG] Extracted magic numbers: " + Arrays.toString(magicNumbers));
                    } else if (argStr.startsWith("extension")) {
                        declaredExtension = extractExtension(arg);
                        System.out.println("[DEBUG] Extracted extension: " + declaredExtension);
                    }
                }

                classesToValidate.add(new ClassToValidate(classTree, path, magicNumbers, declaredExtension));
                System.out.println("[DEBUG] Added class to validation queue: " + classTree.getSimpleName());
                break;
            }
        }
    }

    //simply retrieve extension
    private String extractExtension(ExpressionTree arg) {
        return arg.toString()
                .replace("extension = ", "")
                .replace("\"", "")
                .trim();
    }

    //validate class and return extension
    private String validateAndGetExtension(String className, String declaredExtension) {
        String conventionalExtension = null;
        //check name convention
        if (className.endsWith("DataAccess")) {
            conventionalExtension = className
                    .substring(0, className.length() - "DataAccess".length())
                    .toLowerCase();
        }

        //check declared extension and the one retrieved from the convention (XXXDataAccess pattern)
        if (declaredExtension != null && !declaredExtension.isEmpty()) {
            //if they're not equal, than print a warning and choose the explicit one
            if (conventionalExtension != null && !conventionalExtension.equals(declaredExtension)) {
                System.out.println("\n["+YELLOW+"WARNING"+RESET+"] Ambiguous extension declaration in " + className +
                        ".\n  Class name suggests '" + conventionalExtension +
                        "' but annotation declares '" + declaredExtension +
                        "'.\n  Using declared extension '" + declaredExtension + "'");
            }
            //else if they are equals return the extension
            return declaredExtension;

        } else {
            //if explicit extension is not declared but the class follows the convention
            //return the one extracted from XXXDataAccess
            if (conventionalExtension != null) {
                return conventionalExtension;
            } else {

                //in this case, the class doesn't follow the convention and no explicit
                //extension has been declared, so there is no extension available -> compile error
                throw new RuntimeException("Compilation error: Class " + className +
                        " does not follow XXXDataAccess naming convention and no extension is declared in @ImageAccess annotation");
            }
        }
    }

    //validate class (singleton, interface compatibility etc)
    private void validateCollectedClasses() {
        System.out.println("[DEBUG] Starting class validation");
        Trees trees = Trees.instance(javacTask);
        Elements elements = javacTask.getElements();
        Types types = javacTask.getTypes();

        TypeElement imageDataAccessElement = elements.getTypeElement("ch.supsi.business.image.ImageDataAccess");
        if (imageDataAccessElement == null) {
            System.out.println("[DEBUG] " + RED + "ERROR: ImageDataAccess interface not found!" + RESET);
        } else {
            System.out.println("[DEBUG] Found ImageDataAccess interface");
        }

        for (ClassToValidate classToValidate : classesToValidate) {
            ClassTree classTree = classToValidate.classTree;
            String className = classTree.getSimpleName().toString();
            System.out.println("\n[DEBUG] Validating class: " + className);

            boolean isValid = true;
            StringBuilder validationErrors = new StringBuilder();
            boolean hasValidGetInstance = false;

            // Check getInstance method
            for (Tree member : classTree.getMembers()) {
                if (member instanceof MethodTree) {
                    MethodTree method = (MethodTree) member;
                    System.out.println("[DEBUG] Checking method: " + method.getName());

                    if (method.getName().toString().equals("getInstance")) {
                        System.out.println("[DEBUG] Found getInstance method");
                        boolean isStatic = method.getModifiers().getFlags()
                                .contains(javax.lang.model.element.Modifier.STATIC);
                        System.out.println("[DEBUG] Is static: " + isStatic);

                        Element methodElement = trees.getElement(new TreePath(classToValidate.path, method));
                        if (methodElement instanceof ExecutableElement) {
                            ExecutableElement executableElement = (ExecutableElement) methodElement;
                            TypeMirror returnType = executableElement.getReturnType();

                            boolean hasValidReturnType = types.isAssignable(returnType, imageDataAccessElement.asType());
                            System.out.println("[DEBUG] Has valid return type: " + hasValidReturnType);

                            if (!isStatic) {
                                isValid = false;
                                validationErrors.append("\n[ERROR] getInstance in ").append(className).append(" must be static");
                            }
                            if (!hasValidReturnType) {
                                isValid = false;
                                validationErrors.append("\n[ERROR] getInstance in ").append(className)
                                        .append(" must return a type assignable to ImageDataAccess");
                            }

                            hasValidGetInstance = isStatic && hasValidReturnType;
                        }
                        break;
                    }
                }
            }

            if (!hasValidGetInstance) {
                isValid = false;
                validationErrors.append("\n[ERROR] ").append(className).append(" must provide a valid getInstance method");
            }

            System.out.println("[DEBUG] Class validation result - Is Valid: " + isValid);
            if (!isValid) {
                System.out.println("[DEBUG] Validation errors: " + validationErrors);
                throw new RuntimeException("Compilation error: " + validationErrors.toString());
            }

            try {
                String extension = validateAndGetExtension(className, classToValidate.declaredExtension);
                validatedClasses.add(new AnnotatedClassInfo(className, extension, classToValidate.magicNumbers));
                System.out.println("[DEBUG] Successfully validated class: " + className);
            } catch (RuntimeException e) {
                System.out.println("[DEBUG] " + RED + "Validation failed with error: " + e.getMessage() + RESET);
                throw e;
            }
        }
    }

    //inject generated classes to dataAccesses field
    private void injectStaticInitializer() {
        System.out.println("Generating static initializer...");

        List<JCTree.JCStatement> statements = List.nil();

        //crea liista temp
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

        //crea contatore per avere nomi unici nelle vars
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

            //setta magic numbers
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

            // setta extension
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

            // setta class (campo)
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

            //aggiungo alla tmp list
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

        //assegno lista tmp a dataaccesses
        statements = statements.append(
                maker.Exec(
                        maker.Assign(
                                maker.Ident(names.fromString(registerField.name.toString())),
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


    //extract magic number using regex
    private String[] extractMagicNumbers(AnnotationTree annotation) {
        return annotation.getArguments().stream()
                .filter(arg -> arg.toString().startsWith("magicNumber"))
                .flatMap(arg -> Arrays.stream(arg.toString()
                                .replace("magicNumber = ", "")
                                .replace("{", "")
                                .replace("}", "")
                                .split(","))
                        .map(s -> s.trim().replace("\"", ""))
                        .filter(s -> !s.isEmpty()))
                .toArray(String[]::new);
    }

    //check if the image has an annotation
    private boolean hasImageAccessAnnotation(ClassTree classTree) {
        return classTree.getModifiers().getAnnotations().stream()
                .anyMatch(a -> a.getAnnotationType().toString().endsWith("ImageAccess"));
    }
}
