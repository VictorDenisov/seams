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

    VariableList(ClassOrInterfaceDeclaration classDeclaration, ImportList imports) {
        for (BodyDeclaration bd : classDeclaration.getMembers()) {
            if (bd instanceof FieldDeclaration) {
                FieldDeclaration fd = (FieldDeclaration) bd;
                String type = fd.getType().toString();
                for (VariableDeclarator vardecl : fd.getVariables()) {
                    fieldTypes.put(vardecl.getId().getName(), new ClassTypeStub(type));
                }
            }
        }
        this.imports = imports;
    }

    List<String> getNames() {
        return new ArrayList<String>(fieldTypes.keySet());
    }

    String getFieldTypeAsString(String fieldName) {
        return fieldTypes.get(fieldName).toStringRepresentation();
    }

    String getFieldTypeAsClass(String fieldName) {
        String fieldType = fieldTypes.get(fieldName).toStringRepresentation();
        //TODO check if fieldName is not contain in fieldTypes
        if (TypeFinder.classIsPrimitive(fieldType)) {
            return TypeFinder.getPrimitiveClass(fieldType).getName();
        } else {
            return imports.getClassByShortName(fieldType).toStringRepresentation();
        }
    }

    boolean hasName(String fieldName) {
        return fieldTypes.keySet().contains(fieldName);
    }

    void put(String fieldName, String fieldType) {
        fieldTypes.put(fieldName, new ClassTypeStub(fieldType));
    }

    void put(String fieldName, ClassType clazz) {
        fieldTypes.put(fieldName, clazz);
    }

    void addAll(VariableList list) {
        fieldTypes.putAll(list.fieldTypes);
    }

}
