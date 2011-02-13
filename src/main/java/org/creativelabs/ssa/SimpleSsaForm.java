package org.creativelabs.ssa;

import japa.parser.ast.body.MethodDeclaration;

public class SimpleSsaForm {

    private String methodName;
    private MethodDeclaration form;

    public SimpleSsaForm(String methodName, MethodDeclaration form) {
        this.methodName = methodName;
        this.form = form;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public MethodDeclaration getForm() {
        return form;
    }

    public void setForm(MethodDeclaration form) {
        this.form = form;
    }
}
