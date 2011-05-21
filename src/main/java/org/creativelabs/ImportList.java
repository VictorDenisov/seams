package org.creativelabs;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.type.*;
import japa.parser.ast.body.*;

import org.creativelabs.introspection.*;

import java.util.*;

public class ImportList {

    private List<ImportDeclaration> list = new ArrayList<ImportDeclaration>();

    private ReflectionAbstraction ra = null;

    protected String className;

    public ImportList(ReflectionAbstraction ra, CompilationUnit cu, ClassOrInterfaceDeclaration cd) {
        this(ra, cu);
        if (cu.getPackage() != null) {
            className = cu.getPackage().getName().toString() + "." + cd.getName();
        } else {
            className = cd.getName();
        }
    }

    public ImportList(ReflectionAbstraction ra, CompilationUnit cu) {
        this.ra = ra;
        if (cu.getPackage() != null) {
            list.add(new ImportDeclaration(new NameExpr(cu.getPackage().getName().toString()), false, true));
        }
        list.add(new ImportDeclaration(new NameExpr("java.lang"), false, true));
        if (cu.getImports() != null) {
            list.addAll(cu.getImports());
        }
    }

    public List<String> getImports() {
        List<String> result = new ArrayList<String>();
        for (ImportDeclaration d : list) {
            result.add(d.getName().toString());
        }
        return result;
    }

    private static boolean classIsPrimitive(String className) {
        return "byte".equals(className)
                || "short".equals(className)
                || "int".equals(className)
                || "long".equals(className)
                || "float".equals(className)
                || "double".equals(className)
                || "char".equals(className)
                || "boolean".equals(className)
                || "void".equals(className);
    }

    private String stripGeneric(String shortName) {
        int v = shortName.indexOf("<");
        if (v < 0) {
            return shortName;
        } else {
            return shortName.substring(0, v);
        }
    }

    private String processForNested(String shortName) {
        return shortName.replace('.', '$');
    }

    private ClassType processTypeArguments(ClassOrInterfaceType classType, ClassType clazz) {
        ClassType result = clazz;
        if (classType.getTypeArgs() != null) {
            ClassType[] args = new ClassType[classType.getTypeArgs().size()];
            int p = 0;
            for (Type argType : classType.getTypeArgs()) {
                args[p++] = getClassByType(argType);
            }
            result = ra.substGenericArgs(clazz, args);
        }
        return result;
    }

    public ClassType getClassByClassOrInterfaceType(ClassOrInterfaceType classType) {
        ClassType result = null;
        if (classType.getScope() == null) {
            result = getClassByShortName(classType.toString());
        } else {
            ClassType higher = getClassByClassOrInterfaceType(classType.getScope());
            if (higher instanceof ClassTypeError) {
                result = ra.getClassTypeByName(classType.toString());
            } else {
                result = ra.getNestedClass(higher, classType.getName());
            }
        }
        result = processTypeArguments(classType, result);

        return result;
    }

    public ClassType getClassByType(Type type) {
        if (type instanceof ReferenceType) {
            Type innerType = ((ReferenceType) type).getType();

            if (innerType instanceof ClassOrInterfaceType) {
                ClassOrInterfaceType classType = (ClassOrInterfaceType) innerType;

                ClassType result = getClassByClassOrInterfaceType(classType);

                return ra.addArrayDepth(result, ((ReferenceType) type).getArrayCount());
            } else {
                PrimitiveType classType = (PrimitiveType) innerType;
                ClassType result = getClassByShortName(classType.toString());

                return ra.addArrayDepth(result, ((ReferenceType) type).getArrayCount());
            }
        } else if (type instanceof ClassOrInterfaceType) {
            ClassOrInterfaceType classType = (ClassOrInterfaceType) type;

            ClassType result = getClassByClassOrInterfaceType(classType);

            return result;
        } else if (type instanceof WildcardType) {
            return getClassByShortName("Object");
        } else {
            return getClassByShortName(type.toString());
        }
    }

    private String getScope(String shortName) {
        String scope = shortName;
        if (shortName.indexOf('$') >= 0) {
            scope = shortName.substring(0, shortName.indexOf('$'));
        }
        return scope;

    }

    public ClassType getClassByShortName(String shortName) {
        shortName = stripGeneric(shortName);
        shortName = processForNested(shortName);
        String scope = getScope(shortName);
        if (classIsPrimitive(shortName)) {
            return ra.getClassTypeByName(shortName);
        }
        for (ImportDeclaration id : list) {
            if (id.isAsterisk()) {
                String className = id.getName().toString() + "." + shortName;
                boolean isClassExists = ra.classWithNameExists(className);
                if (isClassExists) {
                    return ra.getClassTypeByName(className);
                }
            } else {
                if (id.getName().getName().equals(scope)) {
                    String className = id.getName().toString();
                    className = className.substring(0, className.length() - scope.length());

                    String fullClassName = className + shortName;
                    if (ra.classWithNameExists(fullClassName)) {
                        return ra.getClassTypeByName(fullClassName);
                    }
                    for (;;) {
                        int ind = fullClassName.lastIndexOf(".");
                        if (ind < 0) {
                            break;
                        }
                        fullClassName = fullClassName.substring(0, ind) + "$" 
                            + fullClassName.substring(ind + 1, fullClassName.length());
                        if (ra.classWithNameExists(fullClassName)) {
                            return ra.getClassTypeByName(fullClassName);
                        }
                    }
                }
            }
        }
        if (ra.classWithNameExists(className + "$" + shortName)) {
            return ra.getClassTypeByName(className + "$" + shortName);
        }
        if (ra.classWithNameExists(shortName)) {
            return ra.getClassTypeByName(shortName);
        }
        ClassType clazz = ra.getClassTypeByName(className);
        ClassType classType = ra.findClassInTypeHierarchy(clazz, shortName);
        if (classType != null) {
            return classType;
        }
        return ra.createErrorClassType("Unknown class: " + shortName);
    }

}
