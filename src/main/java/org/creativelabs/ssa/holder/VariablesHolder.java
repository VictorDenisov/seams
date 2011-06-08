package org.creativelabs.ssa.holder;

import org.creativelabs.copy.Copyable;

import java.util.List;
import java.util.Map;

/**
 * @author azotcsit
 *         Date: 20.05.11
 *         Time: 21:36
 */
public interface VariablesHolder extends Copyable {
    Map<String, Integer> getReadVariables();
    void setReadVariables(Map<String, Integer> readVariables);

    Map<String, Integer> getWriteVariables();
    void setWriteVariables(Map<String, Integer> writeVariables);

    Integer read(String variableName);
    void write(String variableName, Integer index);

    Integer readFrom(String variableName, boolean read);
    void writeTo(String variableName, Integer index, boolean read);

    List<String> getDifferenceInVariables(VariablesHolder holder, boolean read);
    Integer[] getPhiIndexes(VariablesHolder holder, String variableName);

    void increaseIndex(String variableName);
    void increaseIndexIn(String variableName, boolean read);

    boolean containsKey(String name, boolean read);

    void mergeHolders(VariablesHolder... holders);
}
