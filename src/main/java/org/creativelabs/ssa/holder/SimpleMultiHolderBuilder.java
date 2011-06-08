package org.creativelabs.ssa.holder;

import org.creativelabs.graph.condition.Condition;
import org.creativelabs.introspection.ClassType;
import org.creativelabs.typefinder.ImportList;

import java.util.Map;
import java.util.Set;

/**
 * @author azotcsit
 *         Date: 21.05.11
 *         Time: 13:42
 */
public class SimpleMultiHolderBuilder {

    private Condition condition;
    private Set<String> fieldsNames;
    private Set<String> argsNames;
    private Map<String, Integer> variables;

    private ImportList importList;
    private ClassType classType;

    public SimpleMultiHolderBuilder setCondition(Condition condition) {
        this.condition = condition;
        return this;
    }

    public SimpleMultiHolderBuilder setFieldsNames(Set<String> fieldsNames) {
        this.fieldsNames = fieldsNames;
        return this;
    }
    public SimpleMultiHolderBuilder setArgsNames(Set<String> argsNames) {
        this.argsNames = argsNames;
        return this;
    }

    public SimpleMultiHolderBuilder setVariables(Map<String, Integer> variables) {
        this.variables = variables;
        return this;
    }

    public SimpleMultiHolderBuilder setImportList(ImportList importList) {
        this.importList = importList;
        return this;
    }

    public SimpleMultiHolderBuilder setClassType(ClassType classType) {
        this.classType = classType;
        return this;
    }

    private BasicBlockConditionHolder constructConditionHolder() {
        if (condition != null) {
            return new SimpleConditionHolder(condition );
        } else {
            return new SimpleConditionHolder();
        }
    }

    private ClassFieldsHolder constructClassFieldsHolder() {
        if (fieldsNames != null) {
            return new SimpleClassFieldsHolder(fieldsNames);
        } else {
            return new SimpleClassFieldsHolder();
        }
    }

    private MethodArgsHolder constructMethodArgsHolder() {
        if (argsNames != null) {
            return new SimpleMethodArgsHolder(argsNames);
        } else {
            return new SimpleMethodArgsHolder();
        }
    }

    private VariablesHolder constructVariablesHolder() {
        if (variables != null) {
            return new SimpleVariablesHolder(variables);
        } else {
            return new SimpleVariablesHolder();
        }
    }

    private MethodModifiersHolder constructMethodModifiersHolder() {
        if (importList != null && classType != null) {
            return new SimpleMethodModifiersHolder(importList, classType);
        } else {
            return new SimpleMethodModifiersHolder();
        }
    }

    public SimpleMultiHolder buildMultiHolder() {
        return new SimpleMultiHolder(
                constructConditionHolder(),
                constructClassFieldsHolder(),
                constructMethodArgsHolder(),
                constructVariablesHolder(),
                constructMethodModifiersHolder()
        );
    }
}
