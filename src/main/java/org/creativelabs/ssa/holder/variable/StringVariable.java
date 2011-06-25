package org.creativelabs.ssa.holder.variable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.creativelabs.Constants;

/**
 * @author azotcsit
 *         Date: 11.06.11
 *         Time: 21:57
 */
public class StringVariable implements Variable {

    Log log = LogFactory.getLog(StringVariable.class);

    String name;
    String scope;

//    public StringVariable(String name, boolean isArgumentOtherwiseThis) {
//        this.name = name;
//        if (isArgumentOtherwiseThis) {
//            this.scope = Constants.ARG_SCOPE;
//        } else {
//            this.scope = Constants.THIS_SCOPE;
//        }
//    }

    public StringVariable(String name, String scope) {
        this.name = name;
        this.scope = scope;
    }

    @Override
    public String getString() {
        if (Constants.ARG_SCOPE.equals(scope) || Constants.EMPTY_SCOPE.equals(scope)) {
            return name;
        }
        return scope + "." + name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getScope() {
        return scope;
    }

    @Override
    public String toString() {
        return getString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StringVariable that = (StringVariable) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (scope != null ? !scope.equals(that.scope) : that.scope != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (scope != null ? scope.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(Variable o) {
        if (scope == null) {
            return name.compareTo(o.getName());
        }
        int cmp = scope.compareTo(o.getScope());
        if (cmp == 0) {
            return name.compareTo(o.getName());
        }
        return cmp;
    }

    @Override
    public StringVariable copy() {
        return new StringVariable(name, scope);
    }
}
