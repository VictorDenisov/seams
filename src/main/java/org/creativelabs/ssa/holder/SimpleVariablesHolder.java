package org.creativelabs.ssa.holder;

import java.util.*;

/**
 * Stores variable's indexes
 *
 * @author azotcsit
 *         Date: 20.05.11
 *         Time: 22:17
 */
public class SimpleVariablesHolder implements VariablesHolder {

    /**
     * List of variables and they indexes for reading.
     */
    private Map<String, Integer> readVariables;

    /**
     * List of variables and they indexes for updating.
     */
    private Map<String, Integer> writeVariables;

    public SimpleVariablesHolder() {
        readVariables = new TreeMap<String, Integer>();
        writeVariables = new TreeMap<String, Integer>();
    }

    public SimpleVariablesHolder(Map<String, Integer> writeVariables) {
        this.readVariables = copy(writeVariables);
        this.writeVariables = writeVariables;
    }

    public SimpleVariablesHolder(Map<String, Integer> readVariables, Map<String, Integer> writeVariables) {
        this.readVariables = readVariables;
        this.writeVariables = writeVariables;
    }

    public Map<String, Integer> getReadVariables() {
        return readVariables;
    }

    public Map<String, Integer> getWriteVariables() {
        return writeVariables;
    }

    public void setReadVariables(Map<String, Integer> readVariables) {
        this.readVariables = readVariables;
    }

    public void setWriteVariables(Map<String, Integer> writeVariables) {
        this.writeVariables = writeVariables;
    }

    public Integer read(String variableName) {
        return readVariables.get(variableName);
    }

    public Integer readFrom(String variableName, boolean read) {
        return getCurrentVariables(read).get(variableName);
    }

    public void write(String variableName, Integer index) {
        readVariables.put(variableName, index);
        writeVariables.put(variableName, index);
    }

    public void writeTo(String variableName, Integer index, boolean read) {
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
    public List<String> getDifferenceInVariables(VariablesHolder holder, boolean read) {
        List<String> list = new ArrayList<String>();
        Map<String, Integer> map;
        if (read) {
            map = holder.getReadVariables();
        } else {
            map = holder.getWriteVariables();
        }
        for (Map.Entry<String, Integer> entry : getCurrentVariables(read).entrySet()) {
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
    public Integer[] getPhiIndexes(VariablesHolder holder, String variableName) {
        Integer index1 = holder.readFrom(variableName, true);
        Integer index2 = readFrom(variableName, true);
        if (index1 == null) {
            index1 = -1;
        }
        if (index2 == null) {
            index2 = -1;
        }
        return new Integer[] {Math.min(index1, index2),
                Math.max(index1, index2)};
    }

    public boolean containsKey(String name, boolean read) {
        return getCurrentVariables(read).containsKey(name);
    }

    public void increaseIndex(String variableName) {
        if (readVariables.containsKey(variableName)) {
            readVariables.put(variableName, readVariables.get(variableName) + 1);
        }
        if (writeVariables.containsKey(variableName)) {
            writeVariables.put(variableName, writeVariables.get(variableName) + 1);
        }
    }

    public void increaseIndexIn(String variableName, boolean read) {
        if (getCurrentVariables(read).containsKey(variableName)) {
            getCurrentVariables(read).put(variableName, getCurrentVariables(read).get(variableName) + 1);
        }
    }

    /**
     * Merges variables of a different holders. In fact it's selects a maximal indexes for variables.
     *
     * @param holders of type SimpleMultiHolder
     */
    public void mergeHolders(VariablesHolder... holders){
        for (VariablesHolder holder : holders){
            for (Map.Entry<String, Integer> entry : holder.getReadVariables().entrySet()){
                if (readVariables.containsKey(entry.getKey())) {
                    String key = entry.getKey();
                    readVariables.put(key, Math.max(readVariables.get(key), entry.getValue()));
                } else {
                    readVariables.put(entry.getKey(), entry.getValue());
                }
            }
            for (Map.Entry<String, Integer> entry : holder.getWriteVariables().entrySet()){
                if (writeVariables.containsKey(entry.getKey())) {
                    String key = entry.getKey();
                    writeVariables.put(key, Math.max(writeVariables.get(key), entry.getValue()));
                } else {
                    writeVariables.put(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    private Map<String, Integer> copy(Map<String, Integer> map) {
        Map<String, Integer> result = new HashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public VariablesHolder copy() {
        return new SimpleVariablesHolder(copy(readVariables), copy(writeVariables));
    }

    private Set<String> copy(Set<String> arguments) {
        Set<String> args = new HashSet<String>();
        for (String arg : arguments) {
            args.add(arg);
        }
        return args;
    }

    private Map<String, Integer> getCurrentVariables(boolean read) {
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

        SimpleVariablesHolder that = (SimpleVariablesHolder) o;

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
