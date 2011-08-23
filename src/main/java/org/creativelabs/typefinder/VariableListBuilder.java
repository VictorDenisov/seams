package org.creativelabs.typefinder;

import org.creativelabs.introspection.*;

import japa.parser.ast.type.*;
import japa.parser.ast.body.*;

public class VariableListBuilder {

    private ImportList imports;

    private ReflectionAbstraction ra;

    public VariableListBuilder setReflectionAbstraction(ReflectionAbstraction ra) {
        this.ra = ra;
        return this;
    }

    public VariableListBuilder setImports(ImportList imports) {
        this.imports = imports;
        return this;
    }

    public VariableList buildFromMethod(MethodDeclaration methodDeclaration) {
        VariableList result = new VariableList(ra);
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
            result.put(name, classType);
        }
        return result;
    }

    public VariableList buildFromClass(ClassOrInterfaceDeclaration classDeclaration) {
        VariableList result = new VariableList(ra);
        for (BodyDeclaration bd : classDeclaration.getMembers()) {
            if (bd instanceof FieldDeclaration) {
                FieldDeclaration fd = (FieldDeclaration) bd;
                Type type = fd.getType();
                for (VariableDeclarator vardecl : fd.getVariables()) {
                    ClassType classType = imports.getClassByType(type);
                    classType = ra.addArrayDepth(classType, vardecl.getId().getArrayCount());
                    result.put(vardecl.getId().getName(), classType);
                }
            }
        }
        return result;
    }

    public VariableList buildEmpty() {
        return new VariableList(ra);
    }
}
