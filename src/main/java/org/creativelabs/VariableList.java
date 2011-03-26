package org.creativelabs;

import org.creativelabs.introspection.*;

import japa.parser.ast.expr.*;
import japa.parser.ast.type.*;
import japa.parser.ast.body.*;

import java.util.*;

class VariableList {

    protected Map<String, ClassType> fieldTypes = new HashMap<String, ClassType>();

    protected VariableList() {
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
