package org.creativelabs.ssa.holder;

import org.creativelabs.copy.CopyingUtils;
import org.creativelabs.introspection.ClassType;
import org.creativelabs.typefinder.ImportList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author azotcsit
 *         Date: 21.05.11
 *         Time: 11:56
 */
public class SimpleMethodModifiersHolder implements MethodModifiersHolder {

    private ImportList importList;
    private ClassType classType;

    public SimpleMethodModifiersHolder() {
    }

    public SimpleMethodModifiersHolder(ImportList importList, ClassType classType) {
        this.importList = importList;
        this.classType = classType;
    }

    @Override
    public int getModifier(String methodName, List<String> argsNames) {
        int modifier = -1;
        try {
            List<Class> argsTypes = new ArrayList<Class>();
            for (String argName : argsNames) {
                argsTypes.add(Class.forName(importList.getClassByShortName(argName).toString()));
            }
            Class clazz = Class.forName(classType.toString());
            modifier = clazz.getMethod(methodName, argsTypes.toArray(new Class[argsTypes.size()])).getModifiers();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return modifier;
    }

    @Override
    public SimpleMethodModifiersHolder copy() {
        return CopyingUtils.copy(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleMethodModifiersHolder that = (SimpleMethodModifiersHolder) o;

        if (classType != null ? !classType.equals(that.classType) : that.classType != null) return false;
        if (importList != null ? !importList.equals(that.importList) : that.importList != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = importList != null ? importList.hashCode() : 0;
        result = 31 * result + (classType != null ? classType.hashCode() : 0);
        return result;
    }
}