package org.creativelabs;

import japa.parser.ast.expr.*;
import japa.parser.ast.body.*;

import java.util.*;


class VariableList {

    private List<String> fieldNames = new ArrayList<String>();

    private Map<String, String> fieldTypes = new HashMap<String, String>();

    private ImportList imports = null;

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
        this.imports = imports;
    }

    List<String> getNames() {
        return Collections.unmodifiableList(fieldNames);
    }

    String getFieldTypeAsString(String fieldName) {
        return fieldTypes.get(fieldName);
    }

    Class getFieldTypeAsClass(String fieldName) throws Exception {
        String fieldType = fieldTypes.get(fieldName);
        //TODO check if fieldName is not contain in fieldTypes
        if (TypeFinder.classIsPrimitive(fieldType)) {
            return TypeFinder.getPrimitiveClass(fieldType);
        } else {
            Expression expr = ParseHelper.createExpression(fieldType);
            String className = new TypeFinder().determineType(expr, null, imports);
            Class clazz = Class.forName(className);
            return clazz;
        }


    }

    boolean hasName(String fieldName) {
        return fieldTypes.keySet().contains(fieldName);
    }

    void put(String fieldName, String fieldType) {
        fieldNames.add(fieldName);
        fieldTypes.put(fieldName, fieldType);
    }

    void put(String fieldName, Class clazz) {
        fieldNames.add(fieldName);
        fieldTypes.put(fieldName, clazz.getSimpleName());
    }

    void addAll(VariableList list) {
        fieldNames.addAll(list.fieldNames);
        fieldTypes.putAll(list.fieldTypes);
    }

}
