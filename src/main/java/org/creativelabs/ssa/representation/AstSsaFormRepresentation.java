package org.creativelabs.ssa.representation;

import japa.parser.ast.body.MethodDeclaration;

/**
 * Stores ast representation of methods.
 */
public class AstSsaFormRepresentation implements SsaFormRepresentation {

    private MethodDeclaration ast;

    public AstSsaFormRepresentation(MethodDeclaration ast) {
        this.ast = ast;
    }

    public MethodDeclaration getAst() {
        return ast;
    }

    public void setAst(MethodDeclaration ast) {
        this.ast = ast;
    }

    @Override
    public String getSsaFormStringRepresentation() {
        return ast.toString();
    }

    @Override
    public void removeRedundantInformation() {
        removeJavadocComment(ast);
        removeAnnotations(ast);
    }

    private void removeJavadocComment(MethodDeclaration methodDeclaration) {
        methodDeclaration.setJavaDoc(null);
    }

    private void removeAnnotations(MethodDeclaration methodDeclaration) {
        methodDeclaration.setAnnotations(null);
    }
}
