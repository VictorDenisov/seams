package org.creativelabs;

import org.creativelabs.introspection.*;

import japa.parser.ast.expr.*;
import japa.parser.ast.type.*;
import japa.parser.ast.body.*;

import java.util.*;

class VariableList {

    protected ArrayList<Map<String, ClassType>> fieldTypes;

    protected Map<String, ClassType> topFieldTypes;
    
    private ReflectionAbstraction ra;

    protected VariableList(ReflectionAbstraction ra) {
        fieldTypes = new ArrayList<Map<String, ClassType>>();
        topFieldTypes = new HashMap<String, ClassType>();
        fieldTypes.add(topFieldTypes);
        this.ra = ra;
    }

    /**
     * For test purposes only.
     */
    List<String> getNames() {
        return new ArrayList<String>(topFieldTypes.keySet());
    }

    ClassType getFieldTypeAsClass(String fieldName) {
        for (int i = fieldTypes.size() - 1; i >= 0; --i) {
            ClassType result = fieldTypes.get(i).get(fieldName);
            if (result != null) {
                return result;
            }
        }
        return ra.createErrorClassType(fieldName + " doesn't exist");
    }

    /**
     * For test purposes only.
     */
    boolean hasName(String fieldName) {
        return topFieldTypes.keySet().contains(fieldName);
    }

    void put(String fieldName, ClassType clazz) {
        topFieldTypes.put(fieldName, clazz);
    }

    void addAll(VariableList list) {
        for (Map<String, ClassType> map : list.fieldTypes) {
            fieldTypes.add(map);
        }
        topFieldTypes = fieldTypes.get(fieldTypes.size() - 1);
    }

    void incDepth() {
        topFieldTypes = new HashMap<String, ClassType>();
        fieldTypes.add(topFieldTypes);
    }

    void decDepth() {
        fieldTypes.remove(fieldTypes.size() - 1);
        topFieldTypes = fieldTypes.get(fieldTypes.size() - 1);
    }
}
