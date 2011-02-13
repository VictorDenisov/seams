package org.creativelabs;

import org.creativelabs.introspection.*;

import japa.parser.ast.expr.*;
import japa.parser.ast.type.*;
import japa.parser.ast.body.*;

import java.util.*;

public class VariableListBuilder {

    private ImportList imports;

    public VariableListBuilder setImports(ImportList imports) {
        this.imports = imports;
        return this;
    }

    public VariableList buildFromMethod(MethodDeclaration methodDeclaration) {
        VariableList result = new VariableList();
        if (methodDeclaration.getParameters() == null) {
            return result;
        }
        for (Parameter parameter : methodDeclaration.getParameters()) {
            Type type = parameter.getType();
            String name = parameter.getId().getName();
            ClassType classType = imports.getClassByType(type);
            if (parameter.isVarArgs()) {
                classType = ReflectionAbstractionImpl.create().addArrayDepth(classType);
            }
            result.fieldTypes.put(name, classType);
        }
        return result;
    }

    public VariableList buildFromClass(ClassOrInterfaceDeclaration classDeclaration) {
        VariableList result = new VariableList();
        for (BodyDeclaration bd : classDeclaration.getMembers()) {
            if (bd instanceof FieldDeclaration) {
                FieldDeclaration fd = (FieldDeclaration) bd;
                Type type = fd.getType();
                for (VariableDeclarator vardecl : fd.getVariables()) {
                    result.fieldTypes.put(vardecl.getId().getName(), imports.getClassByType(type));
                }
            }
        }
        return result;
    }

    public VariableList buildEmpty() {
        return new VariableList();
    }
}
