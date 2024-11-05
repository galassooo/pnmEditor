package ch.supsi;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.*;
import com.sun.tools.javac.api.BasicJavacTask;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class ImageAccessPlugin implements Plugin {
    private static final Pattern CLASS_NAME_PATTERN = Pattern.compile("PBMDataAccess");
    private final Set<String> processedClasses = new HashSet<>();

    @Override
    public String getName() {
        return "ImageAccessPlugin";
    }

    @Override
    public void init(JavacTask javacTask, String... args) {
        System.out.println("Plugin initialized");
        Context context = ((BasicJavacTask) javacTask).getContext();
        TreeMaker maker = TreeMaker.instance(context);
        Names names = Names.instance(context);

        javacTask.addTaskListener(new TaskListener() {
            @Override
            public void started(TaskEvent e) {
                // Non facciamo nulla qui
            }

            @Override
            public void finished(TaskEvent e) {
                System.out.println("Evento: " + e.getKind());

                if (e.getKind() == TaskEvent.Kind.PARSE) {
                    CompilationUnitTree compilationUnit = e.getCompilationUnit();
                    System.out.println("Processing compilation unit after PARSE");

                    new TreePathScanner<Void, Trees>() {
                        @Override
                        public Void visitClass(ClassTree node, Trees trees) {
                            String className = node.getSimpleName().toString();
                            if (CLASS_NAME_PATTERN.matcher(className).matches() &&
                                    !processedClasses.contains(className)) {

                                System.out.println("Found target class: " + className);
                                processedClasses.add(className);

                                JCTree.JCClassDecl classDecl = (JCTree.JCClassDecl) node;
                                try {
                                    processClass(classDecl, maker, names);
                                } catch (Exception ex) {
                                    System.err.println("Error processing class " + className + ": " + ex.getMessage());
                                    ex.printStackTrace();
                                }
                            }
                            return super.visitClass(node, trees);
                        }
                    }.scan(compilationUnit, Trees.instance(javacTask));
                }
            }
        });
    }

    private void processClass(JCTree.JCClassDecl classDecl, TreeMaker maker, Names names) {
        System.out.println("Processing class: " + classDecl.name);

        for (JCTree member : classDecl.getMembers()) {
            if (member instanceof JCTree.JCVariableDecl) {
                JCTree.JCVariableDecl var = (JCTree.JCVariableDecl) member;
                String fieldName = var.getName().toString();

                System.out.println("Processing field: " + fieldName);

                // Create getter name
                String getterName = "get" +
                        Character.toUpperCase(fieldName.charAt(0)) +
                        fieldName.substring(1);

                // Check if getter already exists
                if (hasMethod(classDecl, getterName)) {
                    System.out.println("Getter already exists for: " + fieldName);
                    continue;
                }

                // Create field access
                JCTree.JCIdent fieldAccess = maker.Ident(var.getName());

                // Create return statement
                JCTree.JCReturn returnStatement = maker.Return(fieldAccess);

                // Create method body
                JCTree.JCBlock methodBody = maker.Block(0, List.of(returnStatement));

                // Create method
                JCTree.JCMethodDecl getter = maker.MethodDef(
                        maker.Modifiers(Flags.PUBLIC),
                        names.fromString(getterName),
                        var.vartype,
                        List.nil(),
                        List.nil(),
                        List.nil(),
                        methodBody,
                        null
                );

                System.out.println("Adding getter: " + getterName);
                classDecl.defs = classDecl.defs.append(getter);
            }
        }
    }

    private boolean hasMethod(JCTree.JCClassDecl classDecl, String methodName) {
        for (JCTree member : classDecl.getMembers()) {
            if (member instanceof JCTree.JCMethodDecl) {
                JCTree.JCMethodDecl method = (JCTree.JCMethodDecl) member;
                if (method.getName().toString().equals(methodName)) {
                    return true;
                }
            }
        }
        return false;
    }
}