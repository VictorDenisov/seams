package org.creativelabs;

import org.creativelabs.introspection.*;

import japa.parser.ast.expr.*;
import japa.parser.ast.type.*;
import japa.parser.ast.body.*;

import java.util.*;


class VariableList {

    private Map<String, ClassType> fieldTypes = new HashMap<String, ClassType>();

    private ImportList imports = null;

    private VariableList() {

    }

    public static VariableList createEmpty() {
        return new VariableList();
    }

    public static VariableList createFromMethodArguments(MethodDeclaration methodDeclaration, ImportList imports) {
        VariableList result = new VariableList();
        result.imports = imports;
        if (methodDeclaration.getParameters() == null) {
            return result;
        }
        for (Parameter parameter : methodDeclaration.getParameters()) {
            Type type = parameter.getType();
            String name = parameter.getId().getName();
            result.fieldTypes.put(name, imports.getClassByType(type));
        }
        return result;
    }

    VariableList(ClassOrInterfaceDeclaration classDeclaration, ImportList imports) {
        this.imports = imports;
        for (BodyDeclaration bd : classDeclaration.getMembers()) {
            if (bd instanceof FieldDeclaration) {
                FieldDeclaration fd = (FieldDeclaration) bd;
                Type type = fd.getType();
                for (VariableDeclarator vardecl : fd.getVariables()) {
                    fieldTypes.put(vardecl.getId().getName(), imports.getClassByType(type));
                }
            }
        }
    }

    List<String> getNames() {
        return new ArrayList<String>(fieldTypes.keySet());
    }

    ClassType getFieldTypeAsClass(String fieldName) {
        ClassType result = fieldTypes.get(fieldName);
        if (result == null) {
            result = new ReflectionAbstractionImpl().createErrorClassType(fieldName + " doesn't exist");
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
