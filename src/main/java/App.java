import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.File;

public class App {
    public static void main(String[] args) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(new File("Sample.java"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        CompilationUnit cu = null;
        try {
            cu = JavaParser.parse(fis);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        for (TypeDeclaration typeDeclaration : cu.getTypes()) {
            if (typeDeclaration instanceof ClassOrInterfaceDeclaration) {
                processClass(typeDeclaration);
            }
        }
    }

    static void processClass(ClassOrInterfaceDeclaration n) {
        Set<String> variables = findFields(n);
        processMethods(n);
    }

    static Set<String> findFields(ClassOrInterfaceDeclaration n) {
        HashSet<String> fields = new HashSet<String>();
        List<BodyDeclaration> members = n.getMembers();
        for (BodyDeclaration bd: members) {
            if (bd instanceof FieldDeclaration) {
                FieldDeclaration fd = (FieldDeclaration) bd;
                for (VariableDeclarator var : fd.getVariables()) {
                    fields.add(var.getId().getName());
                }
            }
        }
        return fields;
    }

    static void processMethods(ClassOrInterfaceDeclaration n) {
        for (BodyDeclaration bd : n.getMembers()) {
            if (bd instanceof MethodDeclaration) {
                MethodDeclaration md = (MethodDeclaration) bd;
                int fanOut = countOutgoingDependencies(md);
            }
        }
    }
    
    static int countOutgoingDependencies(MethodDeclaration md) {
        BlockStmt body  = md.getBody();
        for (Statement stmt : body.getStmts()) {
            SeamCounterVisitor seamCounter = new SeamCounterVisitor();
        }
        
    }

}
