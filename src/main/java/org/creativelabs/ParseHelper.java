package org.creativelabs;

import japa.parser.ast.body.*;
import japa.parser.ast.stmt.*;
import japa.parser.ast.expr.*;
import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class ParseHelper {

    /**
     * data - one expression, which ast should be returned.
     */
    public static Expression createExpression(String data) throws ParseException {
        InputStream sr = new ByteArrayInputStream(("public class Sample {" +
                "public static void main(String[] args) {" +
                data + ";" +
                "}" + 
                "}").getBytes());

        CompilationUnit cu = JavaParser.parse(sr);

        ClassOrInterfaceDeclaration cd = (ClassOrInterfaceDeclaration)cu.getTypes().get(0);
        MethodDeclaration md = (MethodDeclaration) cd.getMembers().get(0);
        Expression expr = ((ExpressionStmt)md.getBody().getStmts().get(0)).getExpression();

        return expr;
    }

    public static CompilationUnit createCompilationUnit(String data) throws ParseException {
        InputStream sr = new ByteArrayInputStream(data.getBytes());

        CompilationUnit cu = JavaParser.parse(sr);

        return cu;
    }

    public static ImportList createImportList(String data) throws Exception {
        CompilationUnit cu = ParseHelper.createCompilationUnit(
                data + " public class Main{}");
        ImportList imports = new ImportList(cu);
        return imports;
    }

    public static ClassOrInterfaceDeclaration createClassDeclaration(String data) throws Exception {
        CompilationUnit cu = ParseHelper.createCompilationUnit(data);
        return (ClassOrInterfaceDeclaration)cu.getTypes().get(0);
    }

    public static BlockStmt createBlockStmt(String data) throws ParseException {
        InputStream sr = new ByteArrayInputStream(("public class Sample {" +
                data +
                "}").getBytes());

        CompilationUnit cu = JavaParser.parse(sr);

        ClassOrInterfaceDeclaration cd = (ClassOrInterfaceDeclaration)cu.getTypes().get(0);
        return ((MethodDeclaration) cd.getMembers().get(0)).getBody();
    }
}
