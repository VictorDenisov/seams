package org.creativelabs;

public class DependencyCounterVisitorBuilder {

    private ImportList imports;

    private VariableList classFields;

    private VariableList methodArguments;

    public DependencyCounterVisitorBuilder setImports(ImportList importsArg) {
        this.imports = importsArg;
        return this;
    }

    public DependencyCounterVisitorBuilder setClassFields(VariableList varsArg) {
        this.classFields = varsArg;
        return this;
    }

    public DependencyCounterVisitorBuilder setMethodArguments(VariableList methodArgsArg) {
        this.methodArguments = methodArgsArg;
        return this;
    }

    public DependencyCounterVisitor build() {
        if (imports == null) {
            throw new IllegalStateException("Imports can't be null");
        }
        if (classFields == null) {
            throw new IllegalStateException("Class fields can't be null");
        }
        if (methodArguments == null) {
            throw new IllegalStateException("Method arguments can't be null");
        }
        return new DependencyCounterVisitor(classFields, imports);
    }
}
