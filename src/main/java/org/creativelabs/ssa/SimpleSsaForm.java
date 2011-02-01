package org.creativelabs.ssa;

public class SimpleSsaForm {

    private String methodName;
    private StringBuilder form;

    public SimpleSsaForm(String methodName, StringBuilder form) {
        this.methodName = methodName;
        this.form = form;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public StringBuilder getForm() {
        return form;
    }

    public void setForm(StringBuilder form) {
        this.form = form;
    }
}
