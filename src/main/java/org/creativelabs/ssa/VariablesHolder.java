package org.creativelabs.ssa;

import org.creativelabs.graph.condition.Condition;
import org.creativelabs.graph.condition.EmptyCondition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO need to create javadoc and tests.

/**
 * Stores variable's indexes
 */
public class VariablesHolder {

    /**
     * List of variables and they indexes for reading.
     */
    private Map<String, Integer> readVariables = new HashMap<String, Integer>();

    /**
     * List of variables and they indexes for updating.
     */
    private Map<String, Integer> writeVariables = new HashMap<String, Integer>();

    private Condition condition = new EmptyCondition();

    public VariablesHolder(Map<String, Integer> readVariables, Map<String, Integer> writeVariables) {
        this.readVariables = readVariables;
        this.writeVariables = writeVariables;
    }

    public VariablesHolder(Map<String, Integer> writeVariables) {
        this.readVariables = copy(writeVariables);
        this.writeVariables = writeVariables;
    }

    public VariablesHolder(Map<String, Integer> writeVariables, Condition condition) {
        this(writeVariables);
        this.condition = condition;
    }

    public VariablesHolder(Map<String, Integer> readVariables, Map<String, Integer> writeVariables, Condition condition) {
        this(readVariables, writeVariables);
        this.condition = condition;
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

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public Integer read(String name) {
        return readVariables.get(name);
    }

    public Integer readFrom(String name, boolean read) {
        return getCurrentVariables(read).get(name);
    }

    public void write(String name, Integer index) {
        readVariables.put(name, index);
        writeVariables.put(name, index);
    }

    public void writeTo(String name, Integer index, boolean read) {
        getCurrentVariables(read).put(name, index);
    }

    /**
     * Returns list of variable's names if they indexes are different.
     * If read is true then search will be in rearVariables otherwise in writeVariables.
     *
     * @param holder holder of type VariablesHolder
     * @param read of type boolean
     * @return List<String>
     */
    public List<String> getDifferenceInVariables(VariablesHolder holder, boolean read) {
        List<String> list = new ArrayList<String>();
        Map<String, Integer> map = holder.getCurrentVariables(read);
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
     * @param holder of type VariablesHolder
     * @param name name of the variable
     * @return Integer[]
     */
    Integer[] getPhiIndexes(VariablesHolder holder, String name) {
        Integer index1 = holder.readFrom(name, true);
        Integer index2 = readFrom(name, true);
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

    public void copyWriteToReadVariables() {
        readVariables = copy(writeVariables);
    }

    public void copyReadToWriteVariables() {
        writeVariables = copy(readVariables);
    }

    public void increaseIndex(String name) {
        if (readVariables.containsKey(name)) {
            readVariables.put(name, readVariables.get(name) + 1);
        }
        if (writeVariables.containsKey(name)) {
            writeVariables.put(name, writeVariables.get(name) + 1);
        }
    }

    public void increaseIndexIn(String name, boolean read) {
        if (getCurrentVariables(read).containsKey(name)) {
            getCurrentVariables(read).put(name, getCurrentVariables(read).get(name) + 1);
        }
    }

    /**
     * Merges variables of a different holders. In fact it's selects a maximal indexes for variables.
     *
     * @param holders of type VariablesHolder
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

    private Condition copy(Condition condition) {
        return new CopyingUtils<Condition>().copy(condition);
    }


    public VariablesHolder copy() {
        VariablesHolder holder = new VariablesHolder(copy(readVariables), copy(writeVariables));
        holder.setCondition(copy(this.getCondition()));
        return holder;
    }

    private Map<String, Integer> getCurrentVariables(boolean read) {
        if (read) {
            return readVariables;
        } else {
            return writeVariables;
        }
    }

}
