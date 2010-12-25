package org.creativelabs;

import org.creativelabs.introspection.*;

import japa.parser.ast.expr.*;
import japa.parser.ast.body.*;

import java.util.*;


class VariableList {

    private Map<String, ClassType> fieldTypes = new HashMap<String, ClassType>();

    private ImportList imports = null;

    VariableList() {
    }

    private ClassType getByClass(String fieldType) {
        //TODO check if fieldName is not contain in fieldTypes
        if (TypeFinder.classIsPrimitive(fieldType)) {
            return new ReflectionAbstractionImpl()
                .getClassTypeByName(TypeFinder.getPrimitiveClass(fieldType).getName());
        } else {
            return imports.getClassByShortName(fieldType);
        }
    }

    VariableList(ClassOrInterfaceDeclaration classDeclaration, ImportList imports) {
        for (BodyDeclaration bd : classDeclaration.getMembers()) {
            if (bd instanceof FieldDeclaration) {
                FieldDeclaration fd = (FieldDeclaration) bd;
                String type = fd.getType().toString();
                for (VariableDeclarator vardecl : fd.getVariables()) {
                    fieldTypes.put(vardecl.getId().getName(), getByClass(type));
                }
            }
        }
        this.imports = imports;
    }

    List<String> getNames() {
        return new ArrayList<String>(fieldTypes.keySet());
    }

    ClassType getFieldTypeAsClass(String fieldName) {
        return fieldTypes.get(fieldName);
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
