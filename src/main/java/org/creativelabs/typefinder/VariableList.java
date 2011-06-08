package org.creativelabs.typefinder;

import org.creativelabs.introspection.*;

import java.util.*;

public class VariableList {

    protected Map<String, ClassType> fieldTypes = new HashMap<String, ClassType>();

    protected VariableList() {
    }

    public List<String> getNames() {
        return new ArrayList<String>(fieldTypes.keySet());
    }

    public ClassType getFieldTypeAsClass(String fieldName) {
        ClassType result = fieldTypes.get(fieldName);
        if (result == null) {
            result = ReflectionAbstractionImpl.create().createErrorClassType(fieldName + " doesn't exist");
        }
        return result;
    }

    public boolean hasName(String fieldName) {
        return fieldTypes.keySet().contains(fieldName);
    }

    public void put(String fieldName, ClassType clazz) {
        fieldTypes.put(fieldName, clazz);
    }

    void addAll(VariableList list) {
        fieldTypes.putAll(list.fieldTypes);
    }

}
