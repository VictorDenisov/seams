package org.creativelabs;

import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        if (!methodArgumentTypes.get(methodName).containsAll(argumentTypes)) {
            throw new TypeFinder.UnsupportedExpressionException();
        }
        String type = methodTypes.get(methodName);
        if (TypeFinder.classIsPrimitive(type)) {
            return TypeFinder.getPrimitiveClass(type).getName();
        }
        return importList.get(type);
    }

}
