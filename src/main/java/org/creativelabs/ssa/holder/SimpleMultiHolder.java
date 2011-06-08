package org.creativelabs.ssa.holder;

import org.creativelabs.graph.condition.Condition;
import org.creativelabs.ssa.PhiNode;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class SimpleMultiHolder implements MultiHolder {

    private BasicBlockConditionHolder conditionHolder;
    private ClassFieldsHolder fieldsHolder;
    private MethodArgsHolder methodArgsHolder;
    private VariablesHolder variablesHolder;
    private MethodModifiersHolder modifiersHolder;
    private PhiNodesHolder phiNodesHolder;

    public SimpleMultiHolder() {
        this.conditionHolder = new SimpleConditionHolder();
        this.fieldsHolder = new SimpleClassFieldsHolder();
        this.variablesHolder = new SimpleVariablesHolder();
        this.modifiersHolder = new SimpleMethodModifiersHolder();
        this.phiNodesHolder = new SimplePhiNodesHolder();
        this.methodArgsHolder = new SimpleMethodArgsHolder();
    }

    public SimpleMultiHolder(BasicBlockConditionHolder conditionHolder, ClassFieldsHolder fieldsHolder, MethodArgsHolder methodArgsHolder, VariablesHolder variablesHolder, MethodModifiersHolder modifiersHolder) {
        this.conditionHolder = conditionHolder;
        this.fieldsHolder = fieldsHolder;
        this.methodArgsHolder = methodArgsHolder;
        this.variablesHolder = variablesHolder;
        this.modifiersHolder = modifiersHolder;
        this.phiNodesHolder = new SimplePhiNodesHolder();
    }

    public SimpleMultiHolder(BasicBlockConditionHolder conditionHolder, ClassFieldsHolder fieldsHolder, MethodArgsHolder methodArgsHolder, VariablesHolder variablesHolder, MethodModifiersHolder modifiersHolder, PhiNodesHolder phiNodesHolder) {
        this.conditionHolder = conditionHolder;
        this.fieldsHolder = fieldsHolder;
        this.methodArgsHolder = methodArgsHolder;
        this.variablesHolder = variablesHolder;
        this.modifiersHolder = modifiersHolder;
        this.phiNodesHolder = phiNodesHolder;
    }

    public BasicBlockConditionHolder getConditionHolder() {
        return conditionHolder;
    }

    public ClassFieldsHolder getFieldsHolder() {
        return fieldsHolder;
    }

    public MethodArgsHolder getMethodArgsHolder() {
        return methodArgsHolder;
    }

    public VariablesHolder getVariablesHolder() {
        return variablesHolder;
    }

    public MethodModifiersHolder getModifiersHolder() {
        return modifiersHolder;
    }

    public PhiNodesHolder getPhiNodesHolder() {
        return phiNodesHolder;
    }

    @Override
    public Condition getBasicBlockCondition() {
        return conditionHolder.getBasicBlockCondition();
    }

    @Override
    public void setBasicBlockCondition(Condition condition) {
        conditionHolder.setBasicBlockCondition(condition);
    }

    @Override
    public void addFieldName(String fieldName) {
        fieldsHolder.addFieldName(fieldName);
    }

    @Override
    public void addFieldNames(Collection<String> fieldNames) {
        fieldsHolder.addFieldNames(fieldNames);
    }

    @Override
    public Set<String> getFieldsNames() {
        return fieldsHolder.getFieldsNames();
    }

    @Override
    public void setFieldsNames(Set<String> fieldsNames) {
        fieldsHolder.setFieldsNames(fieldsNames);
    }

    @Override
    public boolean containsFieldName(String fieldName) {
        return fieldsHolder.containsFieldName(fieldName);
    }

    @Override
    public int getCountOfFieldNames() {
        return fieldsHolder.getCountOfFieldNames();
    }

    @Override
    public void addArgName(String argName) {
        methodArgsHolder.addArgName(argName);
    }

    @Override
    public void addArgNames(Collection<String> argNames) {
        methodArgsHolder.addArgNames(argNames);
    }

    @Override
    public Set<String> getArgsNames() {
        return methodArgsHolder.getArgsNames();
    }

    @Override
    public void setArgsNames(Set<String> fieldsNames) {
        methodArgsHolder.setArgsNames(fieldsNames);
    }

    @Override
    public boolean containsArgName(String argName) {
        return methodArgsHolder.containsArgName(argName);
    }

    @Override
    public int getCountOfArgNames() {
        return methodArgsHolder.getCountOfArgNames();
    }

    @Override
    public void mergeHolders(VariablesHolder... holders) {
        variablesHolder.mergeHolders(holders);
    }

    @Override
    public Map<String, Integer> getReadVariables() {
        return variablesHolder.getReadVariables();
    }

    @Override
    public void setReadVariables(Map<String, Integer> readVariables) {
        variablesHolder.setReadVariables(readVariables);
    }

    @Override
    public Map<String, Integer> getWriteVariables() {
        return variablesHolder.getWriteVariables();
    }

    @Override
    public void setWriteVariables(Map<String, Integer> writeVariables) {
        variablesHolder.setWriteVariables(writeVariables);
    }

    @Override
    public Integer read(String variableName) {
        return variablesHolder.read(variableName);
    }

    @Override
    public void write(String variableName, Integer index) {
        variablesHolder.write(variableName, index);
    }

    @Override
    public Integer readFrom(String variableName, boolean read) {
        return variablesHolder.readFrom(variableName, read);
    }

    @Override
    public void writeTo(String variableName, Integer index, boolean read) {
        variablesHolder.writeTo(variableName, index, read);
    }

    @Override
    public List<String> getDifferenceInVariables(VariablesHolder holder, boolean read) {
        return variablesHolder.getDifferenceInVariables(holder, read);
    }

    @Override
    public Integer[] getPhiIndexes(VariablesHolder holder, String variableName) {
        return variablesHolder.getPhiIndexes(holder, variableName);
    }

    @Override
    public void increaseIndex(String variableName) {
        variablesHolder.increaseIndex(variableName);
    }

    @Override
    public void increaseIndexIn(String variableName, boolean read) {
        variablesHolder.increaseIndexIn(variableName, read);
    }

    @Override
    public boolean containsKey(String name, boolean read) {
        return variablesHolder.containsKey(name, read);
    }

    @Override
    public int getModifier(String methodName, List<String> argsTypes) {
        return modifiersHolder.getModifier(methodName, argsTypes);
    }

    @Override
    public SimpleMultiHolder copy() {
        return new SimpleMultiHolder(
                conditionHolder.<BasicBlockConditionHolder>copy(),
                fieldsHolder.<ClassFieldsHolder>copy(),
                methodArgsHolder.<MethodArgsHolder>copy(),
                variablesHolder.<VariablesHolder>copy(),
                modifiersHolder.<MethodModifiersHolder>copy());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleMultiHolder that = (SimpleMultiHolder) o;

        if (conditionHolder != null ? !conditionHolder.equals(that.conditionHolder) : that.conditionHolder != null)
            return false;
        if (fieldsHolder != null ? !fieldsHolder.equals(that.fieldsHolder) : that.fieldsHolder != null) return false;
        if (methodArgsHolder != null ? !methodArgsHolder.equals(that.methodArgsHolder) : that.methodArgsHolder != null)
            return false;
        if (modifiersHolder != null ? !modifiersHolder.equals(that.modifiersHolder) : that.modifiersHolder != null)
            return false;
        if (phiNodesHolder != null ? !phiNodesHolder.equals(that.phiNodesHolder) : that.phiNodesHolder != null)
            return false;
        if (variablesHolder != null ? !variablesHolder.equals(that.variablesHolder) : that.variablesHolder != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = conditionHolder != null ? conditionHolder.hashCode() : 0;
        result = 31 * result + (fieldsHolder != null ? fieldsHolder.hashCode() : 0);
        result = 31 * result + (methodArgsHolder != null ? methodArgsHolder.hashCode() : 0);
        result = 31 * result + (variablesHolder != null ? variablesHolder.hashCode() : 0);
        result = 31 * result + (modifiersHolder != null ? modifiersHolder.hashCode() : 0);
        result = 31 * result + (phiNodesHolder != null ? phiNodesHolder.hashCode() : 0);
        return result;
    }

    @Override
    public Set<PhiNode> getPhiNodes() {
        return phiNodesHolder.getPhiNodes();
    }

    @Override
    public void setPhiNodes(Set<PhiNode> phiNodes) {
        phiNodesHolder.setPhiNodes(phiNodes);
    }

    @Override
    public void addPhiNodes(Set<PhiNode> phiNodes) {
        phiNodesHolder.addPhiNodes(phiNodes);
    }

    @Override
    public void addPhiNode(PhiNode phiNode) {
        phiNodesHolder.addPhiNode(phiNode);
    }
}
