package org.creativelabs.ssa;

import japa.parser.ast.body.MethodDeclaration;

/**
 * Stores ast representation of methods.
 */
public class SsaFormAstRepresentation {

    private String methodName;
    private MethodDeclaration ast;

    public SsaFormAstRepresentation(String methodName, MethodDeclaration ast) {
        this.methodName = methodName;
        this.ast = ast;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public MethodDeclaration getAst() {
        return ast;
    }

    public void setAst(MethodDeclaration ast) {
        this.ast = ast;
    }
}
