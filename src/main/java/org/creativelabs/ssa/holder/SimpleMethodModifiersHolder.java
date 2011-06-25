package org.creativelabs.ssa.holder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
//TODO: to use it class
public class SimpleMethodModifiersHolder implements MethodModifiersHolder {

    Log log = LogFactory.getLog(SimpleMethodModifiersHolder.class);

    private ImportList importList;
    private ClassType classType;

    public SimpleMethodModifiersHolder() {
    }

    public SimpleMethodModifiersHolder(ImportList importList, ClassType classType) {
        this.importList = importList;
        this.classType = classType;
    }

    @Override
    public int getModifier(String methodName, List<String> argsTypes) {
        int modifier = -1;
        try {
            List<Class> argsTypesCls = new ArrayList<Class>();
            for (String argName : argsTypes) {
                argsTypesCls.add(Class.forName(importList.getClassByShortName(argName).toString()));
            }
            String cl = importList.getClassByShortName(classType.getShortString()).toString();
            Class clazz = Class.forName(cl);
            modifier = clazz.getMethod(methodName, argsTypesCls.toArray(new Class[argsTypesCls.size()])).getModifiers();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        log.error("Error while processing modifier of method[name= " + methodName +
                "] with args[args=" + argsTypes + "]");
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
