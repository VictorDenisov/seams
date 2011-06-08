package org.creativelabs.typefinder;

import org.creativelabs.introspection.*;

import japa.parser.ast.type.*;
import japa.parser.ast.body.*;

public class VariableListBuilder {

    private ImportList imports;

    public VariableListBuilder setImports(ImportList imports) {
        this.imports = imports;
        return this;
    }

    public VariableList buildFromMethod(MethodDeclaration methodDeclaration) {
        VariableList result = new VariableList();
        ReflectionAbstraction ra = ReflectionAbstractionImpl.create();
        if (methodDeclaration.getParameters() == null) {
            return result;
        }
        for (Parameter parameter : methodDeclaration.getParameters()) {
            Type type = parameter.getType();
            String name = parameter.getId().getName();
            ClassType classType = imports.getClassByType(type);
            classType = ra.addArrayDepth(classType, parameter.getId().getArrayCount());

            if (parameter.isVarArgs()) {
                classType = ra.addArrayDepth(classType);
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
                    ClassType classType = imports.getClassByType(type);
                    classType = ReflectionAbstractionImpl.create().convertToArray(classType, vardecl.getId().getArrayCount());
                    result.fieldTypes.put(vardecl.getId().getName(), classType);
                }
            }
        }
        return result;
    }

    public VariableList buildEmpty() {
        return new VariableList();
    }
}
