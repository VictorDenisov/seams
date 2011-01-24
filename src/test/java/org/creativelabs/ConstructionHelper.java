package org.creativelabs;

import org.creativelabs.introspection.*;

public class ConstructionHelper {

    public static VariableList createEmptyVariableList() {
        return VariableList.createEmpty();
    }

    public static VariableList createVarListWithValues(ReflectionAbstraction ra, String... args) {
        VariableList varList = VariableList.createEmpty();
        for (int i = 0; i < args.length / 2; ++i) {
            varList.put(args[i * 2], ra.getClassTypeByName(args[i * 2 + 1]));
        }
        return varList;
    }

    public static ImportList createEmptyImportList() throws Exception{
        return ParseHelper.createImportList("");
    }
}
