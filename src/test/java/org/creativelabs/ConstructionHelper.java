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

    /**
     * java.lang.ClassName[][][] - notation of the class name.
     */
    public static ClassType createClassTypeFromNotation(ReflectionAbstraction ra, String className) {
        int positionOfBracket = className.indexOf("[");
        if (positionOfBracket == -1) {
            return ra.getClassTypeByName(className);
        } else {
            int dimension = (className.length() - positionOfBracket) / 2;
            ClassType classType = ra.getClassTypeByName(className.substring(0, positionOfBracket));
            ClassType result = ra.convertToArray(classType, dimension);
            return result;
        }
    }

    public static VariableList createVarListWithValues(ReflectionAbstraction ra, String... args) {
        VariableList varList = createEmptyVariableList();
        for (int i = 0; i < args.length / 2; ++i) {
            ClassType classType = createClassTypeFromNotation(ra, args[i * 2 + 1]);
            varList.put(args[i * 2], classType);
        }
        return varList;
    }

    public static ImportList createEmptyImportList() throws Exception{
        return ParseHelper.createImportList("");
    }
}
