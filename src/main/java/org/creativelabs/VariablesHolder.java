package org.creativelabs;

import java.lang.reflect.Array;
import java.util.*;

public class VariablesHolder {

    private Map<String, Integer> readVariables = new HashMap<String, Integer>();

    private Map<String, Integer> writeVariables = new HashMap<String, Integer>();

    private Mode mode;

    private boolean isNormalMode = true;

    public VariablesHolder(Map<String, Integer> readVariables, Map<String, Integer> writeVariables, Mode mode) {
        this.readVariables = readVariables;
        this.writeVariables = writeVariables;
        this.mode = mode;
    }

    public VariablesHolder(Map<String, Integer> writeVariables, Mode mode) {
        this.writeVariables = writeVariables;
        this.mode = mode;
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

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public Integer read(String name) {
        return getCurrentVariables(true).get(0).get(name);
    }

    public Integer readFrom(String name, boolean read) {
        if (read) {
            return readVariables.get(name);
        } else {
            return writeVariables.get(name);
        }
    }

    public Integer write(String name, Integer index) {
        if (getCurrentVariables(false).size() == 2) {
            getCurrentVariables(false).get(1).put(name, index);
        }
        return getCurrentVariables(false).get(0).put(name, index);
    }

    public List<String> getDifferenceInVariables(VariablesHolder holder, boolean fromReadVars) {
        List<String> list = new ArrayList<String>();
        for (Map.Entry<String, Integer> entry : holder.getWriteVariables().entrySet()) {
            if (getCurrentVariables(fromReadVars).get(0).containsKey(entry.getKey())
                    && !getCurrentVariables(fromReadVars).get(0).get(entry.getKey()).equals(entry.getValue())) {
                list.add(entry.getKey());
            }
        }
        return list;
    }

    String getPhi(VariablesHolder holder, String name) {
        Integer index1 = holder.read(name);
        Integer index2 = read(name);
        return "phi(" + name + Math.min(index1, index2) +
                "," + name + Math.max(index1, index2) + ")";
    }

    public boolean containsKey(String name, boolean fromRead) {
        return getCurrentVariables(fromRead).get(0).containsKey(name);
    }

    public void copyWriteToReadVariables() {
        readVariables = copy(writeVariables);
    }

    public void copyReadToWriteVariables() {
        writeVariables = copy(readVariables);
    }

    public void increaseIndex(String name) {
        if (getCurrentVariables(false).size() == 2) {
            if (getCurrentVariables(false).get(1).containsKey(name)) {
                getCurrentVariables(false).get(1).put(name, getCurrentVariables(false).get(1).get(name) + 1);
            }
        }
        if (getCurrentVariables(false).get(0).containsKey(name)) {
            getCurrentVariables(false).get(0).put(name, getCurrentVariables(false).get(0).get(name) + 1);
        }
//        return getCurrentVariables(false).get(0).put(name, getCurrentVariables(false).get(0).get(name) + 1);
    }

    private Map<String, Integer> copy(Map<String, Integer> map) {
        Map<String, Integer> result = new HashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }


    VariablesHolder copy() {
        return new VariablesHolder(copy(readVariables), copy(writeVariables), mode);
    }

    private ArrayList<Map<String, Integer>> getCurrentVariables(boolean isRead) {
        if (isRead) {
            switch (mode) {
                case READ_R_VARS_WRITE_R_VARS:
                case READ_R_VARS_WRITE_W_VARS:
                    return new ArrayList<Map<String, Integer>>() {{
                        add(readVariables);
                    }};
                case READ_W_VARS_WRITE_R_VARS:
                case READ_W_VARS_WRITE_W_VARS:
                    return new ArrayList<Map<String, Integer>>() {{
                        add(writeVariables);
                    }};
                case READ_R_VARS_WRITE_WR_VARS:
                    return new ArrayList<Map<String, Integer>>() {{
                        add(readVariables);
                    }};
            }
        } else {
            switch (mode) {
                case READ_R_VARS_WRITE_R_VARS:
                case READ_W_VARS_WRITE_R_VARS:
                    return new ArrayList<Map<String, Integer>>() {{
                        add(readVariables);
                    }};
                case READ_R_VARS_WRITE_W_VARS:
                case READ_W_VARS_WRITE_W_VARS:
                    return new ArrayList<Map<String, Integer>>() {{
                        add(writeVariables);
                    }};
                case READ_R_VARS_WRITE_WR_VARS:
                    return new ArrayList<Map<String, Integer>>() {{
                        add(writeVariables);
                        add(readVariables);
                    }};
            }
        }
        return null;
    }

    public enum Mode {
        READ_W_VARS_WRITE_W_VARS,
        READ_W_VARS_WRITE_R_VARS,
        READ_R_VARS_WRITE_W_VARS,
        READ_R_VARS_WRITE_R_VARS,
        READ_R_VARS_WRITE_WR_VARS,
    }

}
