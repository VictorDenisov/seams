package org.creativelabs;

import japa.parser.ast.body.*;
import japa.parser.ast.expr.*;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import japa.parser.ast.TypeParameter;
import java.util.List;

public class CodeVisitor extends VoidVisitorAdapter<String> {

    @Override
    public void visit(ClassOrInterfaceDeclaration n, String file) {
        System.out.println("Processing class: " + n.getName());
        System.out.println("Type parameters");
        if (n.getTypeParameters() != null) {
            for (TypeParameter typeParameter : n.getTypeParameters()) {
                System.out.println(typeParameter.getName());
            }
        }
        super.visit(n, file);
    }

    @Override
    public void visit(FieldDeclaration n, String file) {
    }

    @Override
    public void visit(MethodDeclaration n, String file) {
        System.out.println("Processing method declaration");
        System.out.println("--------------");
        List<Statement> stmts = n.getBody().getStmts();
        if (stmts != null) {
            for (Statement statement : stmts) {
                System.out.println(statement.toString());
            }
        }
        System.out.println("--------------");
        super.visit(n, file);
    }

    @Override
    public void visit(MethodCallExpr n, String file) {
        System.out.println("We are doing method access");
        System.out.println("Name of the method: " + n.getName());
        System.out.println("Name of the method's scope: " + n.getScope());
        super.visit(n, file);
    }
    @Override
    public void visit(NameExpr n, String file) {
        System.out.println("Visiting name: " + n.getName());
    }

    @Override
    public void visit(FieldAccessExpr n, String file) {
        System.out.println("We are doing field access");
        System.out.println("Name of the field: " + n.getField());
        System.out.println("Name of the field's scope: " + n.getScope());
        super.visit(n, file);
    }
}
