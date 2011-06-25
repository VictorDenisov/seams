package org.creativelabs.ssa.holder;

import org.creativelabs.copy.Copyable;
import org.creativelabs.ssa.holder.variable.Variable;

import java.util.List;
import java.util.Map;

/**
 * @author azotcsit
 *         Date: 20.05.11
 *         Time: 21:36
 */
public interface VariablesHolder extends Copyable {
    Map<Variable, Integer> getReadVariables();
    void setReadVariables(Map<Variable, Integer> readVariables);

    Map<Variable, Integer> getWriteVariables();
    void setWriteVariables(Map<Variable, Integer> writeVariables);

    Integer read(Variable variable);
    void write(Variable variable, Integer index);

    Integer readFrom(Variable variable, boolean read);
    void writeTo(Variable variable, Integer index, boolean read);

    List<Variable> getDifferenceInVariables(VariablesHolder holder, boolean read);
    Integer[] getPhiIndexes(VariablesHolder holder, Variable variable);

    void increaseIndex(Variable variable);
    void increaseIndexIn(Variable variable, boolean read);

    boolean containsKey(Variable variable, boolean read);

    void mergeHolders(VariablesHolder... holders);
}
