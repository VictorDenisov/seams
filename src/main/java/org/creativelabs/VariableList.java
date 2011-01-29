package org.creativelabs;

import org.creativelabs.introspection.*;

import japa.parser.ast.expr.*;
import japa.parser.ast.type.*;
import japa.parser.ast.body.*;

import java.util.*;


class VariableList {

    private Map<String, ClassType> fieldTypes = new HashMap<String, ClassType>();

    protected VariableList() {

    }

    public static VariableList createEmpty() {
        return new VariableList();
    }

    public static VariableList createFromMethodArguments(MethodDeclaration methodDeclaration, ImportList imports) {
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

    public static VariableList createFromClassFields(ClassOrInterfaceDeclaration classDeclaration,
            ImportList imports) {
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

    List<String> getNames() {
        return new ArrayList<String>(fieldTypes.keySet());
    }

    ClassType getFieldTypeAsClass(String fieldName) {
        ClassType result = fieldTypes.get(fieldName);
        if (result == null) {
            result = ReflectionAbstractionImpl.create().createErrorClassType(fieldName + " doesn't exist");
        }
        return result;
    }

    boolean hasName(String fieldName) {
        return fieldTypes.keySet().contains(fieldName);
    }

    void put(String fieldName, ClassType clazz) {
        fieldTypes.put(fieldName, clazz);
    }

    void addAll(VariableList list) {
        fieldTypes.putAll(list.fieldTypes);
    }

}
