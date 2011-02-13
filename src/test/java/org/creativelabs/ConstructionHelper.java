package org.creativelabs;

import org.creativelabs.introspection.*;
import japa.parser.ast.expr.*;
import japa.parser.ast.type.*;
import japa.parser.ast.body.*;

import java.util.*;

public class ConstructionHelper {

    public static VariableList createEmptyVariableList() {
        return new VariableListBuilder().buildEmpty();
    }

    public static VariableList createVariableListFromClassFields(ClassOrInterfaceDeclaration cd,
            ImportList imports) {
        return new VariableListBuilder().setImports(imports).buildFromClass(cd);
    }

    public static VariableList createVariableListFromMethodArgs(MethodDeclaration md,
            ImportList imports) {
        return new VariableListBuilder().setImports(imports).buildFromMethod(md);
    }

    public static VariableList createVarListWithValues(ReflectionAbstraction ra, String... args) {
        VariableList varList = createEmptyVariableList();
        for (int i = 0; i < args.length / 2; ++i) {
            varList.put(args[i * 2], ra.getClassTypeByName(args[i * 2 + 1]));
        }
        return varList;
    }

    public static ImportList createEmptyImportList() throws Exception{
        return ParseHelper.createImportList("");
    }
}
