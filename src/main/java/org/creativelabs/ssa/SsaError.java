package org.creativelabs.ssa;

import japa.parser.ast.body.MethodDeclaration;

import java.io.PrintWriter;

/**
 * @author azotcsit
 *         Date: 25.06.11
 *         Time: 17:29
 */
public class SsaError implements Error {

    private MethodDeclaration methodDeclaration;
    private Exception exception;
    private String className;

    public SsaError(MethodDeclaration methodDeclaration, Exception exception, String className) {
        this.methodDeclaration = methodDeclaration;
        this.exception = exception;
        this.className = className;
    }

    public MethodDeclaration getMethodDeclaration() {
        return methodDeclaration;
    }

    public void setMethodDeclaration(MethodDeclaration methodDeclaration) {
        this.methodDeclaration = methodDeclaration;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public void printError(PrintWriter writer) {
        writer.println(exception);
        writer.println("Class name = " + className);
        writer.println(methodDeclaration);
    }
}
