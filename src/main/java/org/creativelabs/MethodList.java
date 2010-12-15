package org.creativelabs;

import japa.parser.ast.TypeParameter;
import japa.parser.ast.body.*;
import japa.parser.ast.expr.Expression;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: azotcsit
 * Date: 14.12.10
 * Time: 16:47
 * To change this template use File | Settings | File Templates.
 */
public class MethodList {

    private List<String> methodNames = new ArrayList<String>();

    private Map<String, String> methodTypes = new HashMap<String, String>();

    private Map<String, List<String>> methodArgumentTypes = new HashMap<String, List<String>>();


    MethodList() {
    }

    MethodList(ClassOrInterfaceDeclaration classDeclaration) {
        for (BodyDeclaration bd : classDeclaration.getMembers()) {
            if (bd instanceof MethodDeclaration) {
                MethodDeclaration methodDeclaration = (MethodDeclaration) bd;
                String methodName = methodDeclaration.getName();
                methodNames.add(methodName);
                methodTypes.put(methodName, methodDeclaration.getType().toString());
                List<String> argumentTypes = new ArrayList<String>();
                if (methodDeclaration.getParameters() != null) {
                    for (Parameter parameter : methodDeclaration.getParameters()) {
                        argumentTypes.add(parameter.getType().toString());
                    }
                    methodArgumentTypes.put(methodName, argumentTypes);
                }
            }
        }
    }

    String getMethodTypeAsString(String methodName, List<String> argumentTypes, ImportList importList) {
        if (!methodArgumentTypes.get(methodName).containsAll(argumentTypes))
            throw new TypeFinder.UnsupportedExpressionException();
        String type = methodTypes.get(methodName);
        if (PrimitiveClassFactory.getFactory().classIsPrimitive(type)){
            return PrimitiveClassFactory.getFactory().getPrimitiveClass(type).getName();
        }
        return importList.get(type);
    }

}
