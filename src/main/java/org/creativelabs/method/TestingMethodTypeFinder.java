package org.creativelabs.method;

import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TestingMethodTypeFinder implements MethodTypeFinderBuilder {

    private class Wrapper {

        String methodName;

        List<String> argumentsTypes;

        private Wrapper(String methodName, List<String> argumentsTypes) {
            this.methodName = methodName;
            this.argumentsTypes = argumentsTypes;
        }

        public String getMethodName() {
            return methodName;
        }

        public List<String> getArgumentsTypes() {
            return argumentsTypes;
        }
    }

    private Map<Wrapper, String> methods = new HashMap<Wrapper, String>();


    public TestingMethodTypeFinder(ClassOrInterfaceDeclaration classDeclaration) {
        for (BodyDeclaration bd : classDeclaration.getMembers()) {
            if (bd instanceof MethodDeclaration) {
                MethodDeclaration methodDeclaration = (MethodDeclaration) bd;
                String methodName = methodDeclaration.getName();
                String returnType = methodDeclaration.getType().toString();
                List<String> argumentsTypes = new ArrayList<String>();
                if (methodDeclaration.getParameters() != null) {
                    for (Parameter parameter : methodDeclaration.getParameters()) {
                        argumentsTypes.add(parameter.getType().toString());
                    }
                }
                methods.put(new Wrapper(methodName, argumentsTypes), returnType);
            }
        }
    }


    @Override
    public String getMethodTypeAsString(String className, String methodName, Class[] types) throws Exception {
        List<String> argumentsTypes = new ArrayList<String>();
        for (Class argumentType : types) {
            argumentsTypes.add(argumentType.getName());
        }
        return methods.get(new Wrapper(methodName, argumentsTypes));
    }

    @Override
    public Class getMethodTypeAsClass(String className, String methodName, Class[] types) throws Exception {
        List<String> argumentsTypes = new ArrayList<String>();
        for (Class argumentType : types) {
            argumentsTypes.add(argumentType.getName());
        }
        String argumentTypeName = methods.get(new Wrapper(methodName, argumentsTypes));
        return Class.forName(argumentTypeName);
    }

}
