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

        String className;

        String methodName;

        List<String> argumentsTypes;

        private Wrapper(String className, String methodName, List<String> argumentsTypes) {
            this.className = className;
            this.methodName = methodName;
            this.argumentsTypes = argumentsTypes;
        }

        public String getClassName() {
            return className;
        }

        public String getMethodName() {
            return methodName;
        }

        public List<String> getArgumentsTypes() {
            return argumentsTypes;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Wrapper wrapper = (Wrapper) o;

            if (argumentsTypes != null ? !argumentsTypes.equals(wrapper.argumentsTypes) : wrapper.argumentsTypes != null)
                return false;
            if (className != null ? !className.equals(wrapper.className) : wrapper.className != null) return false;
            if (methodName != null ? !methodName.equals(wrapper.methodName) : wrapper.methodName != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = className != null ? className.hashCode() : 0;
            result = 31 * result + (methodName != null ? methodName.hashCode() : 0);
            result = 31 * result + (argumentsTypes != null ? argumentsTypes.hashCode() : 0);
            return result;
        }
    }



    private Map<Wrapper, String> methods = new HashMap<Wrapper, String>();


    public TestingMethodTypeFinder(ClassOrInterfaceDeclaration classDeclaration) {
        String className = classDeclaration.getName();
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
                methods.put(new Wrapper(className, methodName, argumentsTypes), returnType);
            }
        }
    }


    @Override
    public String getMethodTypeAsString(String className, String methodName, Class[] types) throws Exception {
        List<String> argumentsTypes = new ArrayList<String>();
        for (Class argumentType : types) {
            argumentsTypes.add(argumentType.getName());
        }
        return methods.get(new Wrapper(className, methodName, argumentsTypes));
    }

    @Override
    public Class getMethodTypeAsClass(String className, String methodName, Class[] types) throws Exception {
        List<String> argumentsTypes = new ArrayList<String>();
        for (Class argumentType : types) {
            argumentsTypes.add(argumentType.getName());
        }
        String argumentTypeName = methods.get(new Wrapper(className, methodName, argumentsTypes));
        return Class.forName(argumentTypeName);
    }

}
