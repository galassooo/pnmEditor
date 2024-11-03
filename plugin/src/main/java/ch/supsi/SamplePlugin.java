package ch.supsi;

import com.sun.source.tree.*;
import com.sun.source.util.*;
import com.sun.tools.javac.api.BasicJavacTask;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;

public class SamplePlugin implements Plugin {
    @Override
    public String getName() {
        return "MyPlugin";
    }

    @Override
    public void init(JavacTask task, String... args) {
        Context context = ((BasicJavacTask) task).getContext();
        TreeMaker treeMaker = TreeMaker.instance(context);
        Names names = Names.instance(context);

        task.addTaskListener(new TaskListener() {
            @Override
            public void started(TaskEvent e) {
                System.out.println("PLUGIN STARTED E' CARICATOOOOO");
            }

            @Override
            public void finished(TaskEvent e) {
                if (e.getKind() == TaskEvent.Kind.PARSE) {
                    e.getCompilationUnit().accept(new TreeScanner<Void, Void>() {
                        @Override
                        public Void visitClass(ClassTree node, Void aVoid) {
                            // Controlla se la classe ha l'annotazione ImageAccess
                            boolean hasImageAccessAnnotation = false;
                            for (AnnotationTree annotation : node.getModifiers().getAnnotations()) {
                                if (annotation.getAnnotationType().toString().endsWith("ImageAccess")) {
                                    hasImageAccessAnnotation = true;
                                    break;
                                }
                            }

                            if (hasImageAccessAnnotation) {
                                JCTree.JCClassDecl classDecl = (JCTree.JCClassDecl) node;
                                System.out.println("Adding getExtension method to class: " + classDecl.name);
                                // Crea il metodo getExtension
                                JCTree.JCMethodDecl getExtensionMethod = treeMaker.MethodDef(
                                        treeMaker.Modifiers(1), // public = 1
                                        names.fromString("getExtension"), // nome metodo
                                        treeMaker.Ident(names.fromString("String")), // tipo di ritorno String
                                        List.nil(), // parametri di tipo
                                        List.nil(), // parametri
                                        List.nil(), // throws
                                        // corpo del metodo
                                        treeMaker.Block(1, List.of(
                                                treeMaker.Return(
                                                        treeMaker.Literal(getMagicNumberExtension(node))
                                                )
                                        )),
                                        null // default value
                                );

                                // Aggiungi il metodo alla classe
                                classDecl.defs = classDecl.defs.append(getExtensionMethod);
                            }

                            return super.visitClass(node, aVoid);
                        }
                    }, null);
                }
            }
        });
    }

    private String getMagicNumberExtension(ClassTree classTree) {
        for (AnnotationTree annotation : classTree.getModifiers().getAnnotations()) {
            if (annotation.getAnnotationType().toString().endsWith("ImageAccess")) {
                // Assumiamo che il primo magic number definisce l'estensione
                return annotation.toString().contains("P1") ? "pbm" :
                        annotation.toString().contains("P2") ? "pgm" :
                                annotation.toString().contains("P3") ? "ppm" :
                                        "unknown";
            }
        }
        return "unknown";
    }
}
