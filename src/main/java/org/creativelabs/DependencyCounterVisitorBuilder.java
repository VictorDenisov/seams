package org.creativelabs;

import japa.parser.ast.body.*;
import japa.parser.ast.stmt.*;
import japa.parser.ast.expr.*;

public class DependencyCounterVisitorBuilder {

    protected ImportList imports;

    protected VariableList classFields;

    protected VariableList methodArguments;

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

    public DependencyCounterVisitorBuilder setConstructedMethodArguments(MethodDeclaration md) {
        if (imports == null) {
            throw new IllegalStateException("Imports should be not null for this operation");
        }
        this.methodArguments = VariableList.createFromMethodArguments(md, imports);
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
        VariableList externalVariables = VariableList.createEmpty();
        externalVariables.addAll(classFields);
        externalVariables.addAll(methodArguments);
        return new DependencyCounterVisitor(externalVariables, imports);
    }
}
