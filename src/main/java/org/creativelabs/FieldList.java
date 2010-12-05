package org.creativelabs;

import japa.parser.ast.body.*;
import java.util.*;


class FieldList {

    List<String> fieldNames = new ArrayList<String>();

    Map<String, String> fieldTypes = new HashMap<String, String>();

    FieldList(ClassOrInterfaceDeclaration classDeclaration, ImportList imports) {
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
}
