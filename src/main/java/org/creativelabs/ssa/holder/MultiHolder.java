package org.creativelabs.ssa.holder;

/**
 * @author azotcsit
 *         Date: 20.05.11
 *         Time: 23:05
 */
public interface MultiHolder
        extends VariablesHolder, ClassFieldsHolder, MethodArgsHolder, BasicBlockConditionHolder, MethodModifiersHolder, PhiNodesHolder {

    BasicBlockConditionHolder getConditionHolder();
    ClassFieldsHolder getFieldsHolder();
    MethodArgsHolder getMethodArgsHolder();
    VariablesHolder getVariablesHolder();
    MethodModifiersHolder getModifiersHolder();
    PhiNodesHolder getPhiNodesHolder();

}
