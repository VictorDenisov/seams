package org.creativelabs.ssa.holder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.creativelabs.ssa.holder.variable.Variable;

import java.util.*;

/**
 * @author azotcsit
 *         Date: 13.06.11
 *         Time: 9:30
 */
public class ScopeVariablesHolder implements VariablesHolder {

    Log log = LogFactory.getLog(ScopeVariablesHolder.class);

     /**
     * List of variables and they indexes for reading.
     */
    private Map<Variable, Integer> readVariables;

    /**
     * List of variables and they indexes for updating.
     */
    private Map<Variable, Integer> writeVariables;

    public ScopeVariablesHolder() {
        readVariables = new TreeMap<Variable, Integer>();
        writeVariables = new TreeMap<Variable, Integer>();
    }

    public ScopeVariablesHolder(Map<Variable, Integer> writeVariables) {
        this.readVariables = copy(writeVariables);
        this.writeVariables = writeVariables;
    }

    public ScopeVariablesHolder(Map<Variable, Integer> readVariables, Map<Variable, Integer> writeVariables) {
        this.readVariables = readVariables;
        this.writeVariables = writeVariables;
    }

    public Map<Variable, Integer> getReadVariables() {
        return readVariables;
    }

    public Map<Variable, Integer> getWriteVariables() {
        return writeVariables;
    }

    public void setReadVariables(Map<Variable, Integer> readVariables) {
        this.readVariables = readVariables;
    }

    public void setWriteVariables(Map<Variable, Integer> writeVariables) {
        this.writeVariables = writeVariables;
    }

    public Integer read(Variable variableName) {
        return readVariables.get(variableName);
    }

    public Integer readFrom(Variable variableName, boolean read) {
        return getCurrentVariables(read).get(variableName);
    }

    public void write(Variable variableName, Integer index) {
        readVariables.put(variableName, index);
        writeVariables.put(variableName, index);
    }

    public void writeTo(Variable variableName, Integer index, boolean read) {
        getCurrentVariables(read).put(variableName, index);
    }

    /**
     * Returns list of variable's names if they indexes are different.
     * If read is true then search will be in rearVariables otherwise in writeVariables.
     *
     * @param holder holder of type SimpleMultiHolder
     * @param read of type boolean
     * @return List<String>
     */
    public List<Variable> getDifferenceInVariables(VariablesHolder holder, boolean read) {
        List<Variable> list = new ArrayList<Variable>();
        Map<Variable, Integer> map;
        if (read) {
            map = holder.getReadVariables();
        } else {
            map = holder.getWriteVariables();
        }
        for (Map.Entry<Variable, Integer> entry : getCurrentVariables(read).entrySet()) {
            if (map.containsKey(entry.getKey())
                    && !map.get(entry.getKey()).equals(entry.getValue())) {
                list.add(entry.getKey());
            }
        }
        return list;
    }

    /**
     * Returns the indexes of variable. If the variable isn't contained in holder it will be return  -1.
     * @param holder of type SimpleMultiHolder
     * @param variableName name of the variable
     * @return Integer[]
     */
    public Integer[] getPhiIndexes(VariablesHolder holder, Variable variableName) {
        Integer index1 = holder.readFrom(variableName, true);
        Integer index2 = readFrom(variableName, true);
        if (index1 == null) {
            index1 = -1;
            log.error("Error while getting phi indexes for variable = " + variableName.getString());
        }
        if (index2 == null) {
            index2 = -1;
            log.error("Error while getting phi indexes for variable = " + variableName.getString());
        }
        return new Integer[] {Math.min(index1, index2),
                Math.max(index1, index2)};
    }

    public boolean containsKey(Variable name, boolean read) {
        return getCurrentVariables(read).containsKey(name);
    }

    public void increaseIndex(Variable variableName) {
        if (readVariables.containsKey(variableName)) {
            readVariables.put(variableName.<Variable>copy(), readVariables.get(variableName) + 1);
        } else {
            log.warn("Error while increase phi index for variable = " + variableName.getString());
        }
        if (writeVariables.containsKey(variableName)) {
            writeVariables.put(variableName.<Variable>copy(), writeVariables.get(variableName) + 1);
        } else {
            log.warn("Error while increase phi index for variable = " + variableName.getString());
        }
    }

    @Override
    public void increaseIndexIn(Variable variableName, boolean read) {
        if (getCurrentVariables(read).containsKey(variableName)) {
            getCurrentVariables(read).put(variableName, getCurrentVariables(read).get(variableName) + 1);
        } else {
            log.warn("Error while increase phi index for variable = " + variableName.getString());
        }
    }

    /**
     * Merges variables of a different holders. In fact it's selects a maximal indexes for variables.
     *
     * @param holders of type SimpleMultiHolder
     */
    public void mergeHolders(VariablesHolder... holders){
        for (VariablesHolder holder : holders){
            for (Map.Entry<Variable, Integer> entry : holder.getReadVariables().entrySet()){
                if (readVariables.containsKey(entry.getKey())) {
                    Variable key = entry.getKey().copy();
                    readVariables.put(key, Math.max(readVariables.get(key), entry.getValue()));
                } else {
                    readVariables.put(entry.getKey(), entry.getValue());
                }
            }
            for (Map.Entry<Variable, Integer> entry : holder.getWriteVariables().entrySet()){
                if (writeVariables.containsKey(entry.getKey())) {
                    Variable key = entry.getKey().copy();
                    writeVariables.put(key, Math.max(writeVariables.get(key), entry.getValue()));
                } else {
                    writeVariables.put(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    private Map<Variable, Integer> copy(Map<Variable, Integer> map) {
        Map<Variable, Integer> result = new HashMap<Variable, Integer>();
        for (Map.Entry<Variable, Integer> entry : map.entrySet()) {
            result.put(entry.getKey().<Variable>copy(), entry.getValue());
        }
        return result;
    }

    public ScopeVariablesHolder copy() {
        return new ScopeVariablesHolder(copy(readVariables), copy(writeVariables));
    }

    private Set<Variable> copy(Set<Variable> arguments) {
        Set<Variable> args = new HashSet<Variable>();
        for (Variable arg : arguments) {
            args.add(arg.<Variable>copy());
        }
        return args;
    }

    private Map<Variable, Integer> getCurrentVariables(boolean read) {
        if (read) {
            return readVariables;
        } else {
            return writeVariables;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ScopeVariablesHolder that = (ScopeVariablesHolder) o;

        if (readVariables != null ? !readVariables.equals(that.readVariables) : that.readVariables != null)
            return false;
        if (writeVariables != null ? !writeVariables.equals(that.writeVariables) : that.writeVariables != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = readVariables != null ? readVariables.hashCode() : 0;
        result = 31 * result + (writeVariables != null ? writeVariables.hashCode() : 0);
        return result;
    }
}
