package org.creativelabs;

import japa.parser.ast.body.*;
import java.util.*;


class VariableList {

    private List<String> fieldNames = new ArrayList<String>();

    private Map<String, String> fieldTypes = new HashMap<String, String>();

    VariableList() {
    }

    VariableList(ClassOrInterfaceDeclaration classDeclaration, ImportList imports) {
        for (BodyDeclaration bd : classDeclaration.getMembers()) {
            if (bd instanceof FieldDeclaration) {
                FieldDeclaration fd = (FieldDeclaration) bd;
                String type = fd.getType().toString();
                for (VariableDeclarator vardecl : fd.getVariables()) {
                    fieldNames.add(vardecl.getId().getName());
                    fieldTypes.put(vardecl.getId().getName(), type);
                }
            }
        }
    }

    List<String> getNames() {
        return Collections.unmodifiableList(fieldNames);
    }

    String getFieldTypeAsString(String fieldName) {
        return fieldTypes.get(fieldName);
    }

    boolean hasName(String fieldName) {
        return fieldTypes.keySet().contains(fieldName);
    }

    void put(String fieldName, String fieldType) {
        fieldNames.add(fieldName);
        fieldTypes.put(fieldName, fieldType);
    }

    void addAll(VariableList list) {
        fieldNames.addAll(list.fieldNames);
        fieldTypes.putAll(list.fieldTypes);
    }
 
}
