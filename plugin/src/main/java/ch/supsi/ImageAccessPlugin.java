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

    private JCTree.JCClassDecl factoryClassDecl = null;
    private TreeMaker maker = null;
    private Names names = null;
    private boolean codeGenerated = false;
    private JavacTask javacTask = null;


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
        //get context, maker, names and task
        Context context = ((BasicJavacTask) task).getContext();
        this.maker = TreeMaker.instance(context);
        this.names = Names.instance(context);
        this.javacTask = task;

        //select parse as event: (sono i cicli di vita del compilatore in breve)
        //event hierarchy:
        // Parse (create tree)
        // Enter (methods declaration and get symbols)
        // Annotation processing (special step to analyze and parse annotations)
        // Analyze (resolve references)
        // Generate (generate bytecode)
        // Complete (finished compilation)

        //usa parse per modificare il bytecode (video ucraino)
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
                                //get dataAccessFactory node
                                if (node.getSimpleName().toString().equals("DataAccessFactory")) {
                                    factoryClassDecl = (JCTree.JCClassDecl) node;
                                }
                                //let parent parse the node
                                return super.visitClass(node, trees);
                            }
                        }.scan(cu, trees);
                    }

                    //get annotated class (NOT CLASSES!!!!!!!!!!!!!)
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
                            //let super visit class
                            return super.visitClass(node, trees);
                        }
                    }.scan(cu, trees);
                }
                //IF tree has been created (ENTER PHASE) we have a factory, and we haven't generated code yet
                else if (e.getKind() == TaskEvent.Kind.ENTER && !codeGenerated && factoryClassDecl != null) {
                    validateCollectedClasses();
                    injectStaticInitializer();
                    codeGenerated = true;
                }
            }
        });
    }

    //validate class
    private void collectClassForValidation(ClassTree classTree, TreePath path) {
        //get all annotated classes from classTree
        for (AnnotationTree annotation : classTree.getModifiers().getAnnotations()) {
            //get only classes annotated with  imageaccess
            if (annotation.getAnnotationType().toString().endsWith("ImageAccess")) {
                //init util vars
                String[] magicNumbers = null;
                String declaredExtension = null;

                //get annotation arguments
                for (ExpressionTree arg : annotation.getArguments()) {
                    String argStr = arg.toString();
                    //extract magic number if it is present
                    if (argStr.startsWith("magicNumber")) {
                        magicNumbers = extractMagicNumbers(annotation);

                        //extract extension if it is present
                    } else if (argStr.startsWith("extension")) {
                        declaredExtension = extractExtension(arg);
                    }
                }
                //add annotated class to 'tobevalidated' list
                classesToValidate.add(new ClassToValidate(classTree, path, magicNumbers, declaredExtension));
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
        //get tree
        Trees trees = Trees.instance(javacTask);
        //get elements and solved types
        Elements elements = javacTask.getElements();
        Types types = javacTask.getTypes();

        TypeElement imageDataAccessElement = elements.getTypeElement("ch.supsi.business.image.ImageDataAccess");
        if (imageDataAccessElement == null) {
            throw new RuntimeException("Compilation error: Could not find ImageDataAccess interface");
        }

        //cycle class to validate them
        for (ClassToValidate classToValidate : classesToValidate) {
            ClassTree classTree = classToValidate.classTree;
            String className = classTree.getSimpleName().toString();
            boolean isValid = true;
            String validationErrors = "";

            boolean hasValidGetInstance = false;
            //check get instance method
            for (Tree member : classTree.getMembers()) {
                //get method tree
                if (member instanceof MethodTree) {
                    MethodTree method = (MethodTree) member;

                    //check if method exists by name and if it is static
                    if (method.getName().toString().equals("getInstance")) {
                        boolean isStatic = method.getModifiers().getFlags()
                                .contains(javax.lang.model.element.Modifier.STATIC);

                        //get method elements to get return type
                        Element methodElement = trees.getElement(new TreePath(classToValidate.path, method));
                        if (methodElement instanceof ExecutableElement) {
                            ExecutableElement executableElement = (ExecutableElement) methodElement;

                            //mirror type for return type
                            TypeMirror returnType = executableElement.getReturnType();

                            //check interface compatibility on return type of getInstance
                            boolean hasValidReturnType = types.isAssignable(returnType, imageDataAccessElement.asType());

                            // If getInstance is not static, add a warning
                            if (!isStatic) {
                                isValid = false;
                                validationErrors += "\n["+YELLOW+"WARNING"+RESET+"] getInstance in " + className + " must be static";
                            }
                            if (!hasValidReturnType) {
                                isValid = false;
                                validationErrors += "\n["+YELLOW+"WARNING"+RESET+"] getInstance in " + className +
                                        " must return a type assignable to ImageDataAccess";
                            }

                            hasValidGetInstance = isStatic && hasValidReturnType;
                        }
                        break;
                    }
                }
            }

            //if the getInstance is not valid
            if (!hasValidGetInstance) {
                isValid = false;
                validationErrors += "\n["+YELLOW+"WARNING"+RESET+"]" + className + " must provide a valid getInstance method";
            }

            //print all messages and skip class from process
            if (!isValid) {
                System.out.println(validationErrors);
                System.out.println("["+BLUE+"INFO"+RESET+"] Class " + className + " will not be included in static initialization");
                continue;
            }

            //otherwise is a valid class, so process it
            try {

                //validate extension (cannot be invalid, if it fails it throws an ex)
                String extension = validateAndGetExtension(className, classToValidate.declaredExtension);
                //add to validated class
                validatedClasses.add(new AnnotatedClassInfo(className, extension, classToValidate.magicNumbers));
                System.out.println("["+BLUE+"INFO"+RESET+"]+Valid class " + className + " with magic numbers: " +
                        Arrays.toString(classToValidate.magicNumbers) + " and extension: " + extension);
            } catch (RuntimeException e) {
                System.err.println(e.getMessage());
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
                                maker.Ident(names.fromString("dataAccesses")),
                                maker.Ident(names.fromString("tempList"))
                        )
                )
        );

        //creo il blocco con gli statements
        JCTree.JCBlock staticBlock = maker.Block(
                Flags.STATIC,
                statements
        );

        //lo assegno alla dichiarazione
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
