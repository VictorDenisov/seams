package org.creativelabs.graph.condition;

/**
 * @author azotcsit
 * Date: 09.04.11
 * Time: 16:38
 */

public class EmptyCondition implements Condition {

    @Override
    public String getStringRepresentation() {
        return "";
    }

    @Override
    public Condition and(Condition condition) {
        return condition;
    }

    @Override
    public Condition or(Condition condition) {
        return condition;
    }

    @Override
    public Condition not() {
        return this;
    }

    @Override
    public Condition copy() {
        return new EmptyCondition();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EmptyCondition that = (EmptyCondition) o;

        if (getStringRepresentation() != null ?
                !getStringRepresentation().equals(that.getStringRepresentation()) :
                that.getStringRepresentation() != null)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        return getStringRepresentation().hashCode();
    }
}
