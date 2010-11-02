package org.creativelabs;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.File;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.PrimitiveType;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import org.apache.log4j.Logger;

public class App {
    private static Logger logger = Logger.getLogger(App.class);
    private static String fileName;

    private static void peekValuesFromArgs(String[] args) {
        logger.trace("Peeking values");
        if (args.length == 0) {
            System.out.println("File name is expected");
            return;
        }

        fileName = args[0];
    }
    public static void main(String[] args) throws ParseException, FileNotFoundException {
        peekValuesFromArgs(args);

        FileInputStream fis = new FileInputStream(new File(fileName));

        CompilationUnit cu = JavaParser.parse(fis);

        for (TypeDeclaration typeDeclaration : cu.getTypes()) {
            if (typeDeclaration instanceof ClassOrInterfaceDeclaration) {
                processClass((ClassOrInterfaceDeclaration)typeDeclaration);
            }
        }
    }

    static void processClass(ClassOrInterfaceDeclaration n) {
        Map<String, String> variables = findFields(n);
        processMethods(n, variables);
    }

    static class TypeVisitor extends VoidVisitorAdapter<Object> {
        private String name;

        @Override
        public void visit(ClassOrInterfaceType n, Object a) {
            this.name = n.getName();
        }

        @Override
        public void visit(PrimitiveType n, Object a) {
            this.name = n.getType().toString();
        }
    }

    static HashMap<String, String> findFields(ClassOrInterfaceDeclaration n) {
        HashMap<String, String> fields = new HashMap<String, String>();
        List<BodyDeclaration> members = n.getMembers();
        for (BodyDeclaration bd: members) {
            if (bd instanceof FieldDeclaration) {
                FieldDeclaration fd = (FieldDeclaration) bd;
                for (VariableDeclarator var : fd.getVariables()) {
                    TypeVisitor tv = new TypeVisitor();
                    fd.getType().accept(tv, null);
                    fields.put(var.getId().getName(), tv.name);
                }
            }
        }
        return fields;
    }

    static void processMethods(ClassOrInterfaceDeclaration n, Map<String, String> variables) {
        for (BodyDeclaration bd : n.getMembers()) {
            if (bd instanceof MethodDeclaration) {
                MethodDeclaration md = (MethodDeclaration) bd;
                int fanOut = countOutgoingDependencies(md, variables);
            }
        }
    }
    
    static int countOutgoingDependencies(MethodDeclaration md, Map<String, String> variables) {
        System.out.println("------Processing method " + md.getName());
        BlockStmt body  = md.getBody();
        SeamCounterVisitor seamCounter = new SeamCounterVisitor(variables);
        seamCounter.visit(body, null);
        logger.debug("Assigned variables are: " + seamCounter.getAssignedVariables());
        logger.debug("Local variables are: " + seamCounter.getLocalVariables());
        System.out.println("------End of method processing " + md.getName());
        return 0;
    }
}
