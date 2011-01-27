package org.creativelabs;

import java.lang.reflect.Array;
import java.util.*;

public class VariablesHolder {

    private Map<String, Integer> readVariables = new HashMap<String, Integer>();

    private Map<String, Integer> writeVariables = new HashMap<String, Integer>();


    public VariablesHolder(Map<String, Integer> readVariables, Map<String, Integer> writeVariables) {
        this.readVariables = readVariables;
        this.writeVariables = writeVariables;
    }

    public VariablesHolder(Map<String, Integer> writeVariables) {
        this.readVariables = copy(writeVariables);
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

    String getPhi(VariablesHolder holder, String name) {
        Integer index1 = holder.readFrom(name, false);
        Integer index2 = readFrom(name, false);
        return "phi(" + name + Math.min(index1, index2) +
                "," + name + Math.max(index1, index2) + ")";
    }

    String getPhiFrom(VariablesHolder holder, String name, boolean read) {
        Integer index1 = holder.readFrom(name, read);
        Integer index2 = readFrom(name, read);
        return "phi(" + name + Math.min(index1, index2) +
                "," + name + Math.max(index1, index2) + ")";
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

    private Map<String, Integer> copy(Map<String, Integer> map) {
        Map<String, Integer> result = new HashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }


    VariablesHolder copy() {
        return new VariablesHolder(copy(readVariables), copy(writeVariables));
    }

    private Map<String, Integer> getCurrentVariables(boolean read) {
        if (read) {
            return readVariables;
        } else {
            return writeVariables;
        }
    }

}
