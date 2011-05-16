package org.creativelabs.graph.condition;

/**
 * @author azotcsit
 *         Date: 08.04.11
 *         Time: 22:21
 */
public class StringCondition implements Condition {

    private StringBuilder condition;

    public StringCondition(String condition) {
        this.condition = new StringBuilder()
                .append("(")
                .append(condition)
                .append(")");
    }

    private StringCondition(StringBuilder condition) {
        this.condition = new StringBuilder(condition);
    }

    @Override
    public String getStringRepresentation() {
        return condition.toString();
    }

    @Override
    public Condition and(Condition condition) {
        if (!(condition instanceof EmptyCondition)) {
            return new StringCondition(new StringBuilder().append("(")
                    .append(this.condition)
                    .append("&&")
                    .append(condition.getStringRepresentation())
                    .append(")"));
        }
        return this.copy();
    }

    @Override
    public Condition or(Condition condition) {
        if (!(condition instanceof EmptyCondition)) {
            return new StringCondition(new StringBuilder().append("(")
                    .append(this.condition)
                    .append("||")
                    .append(condition.getStringRepresentation())
                    .append(")"));
        }
        return this.copy();
    }

    @Override
    public Condition not() {
        return new StringCondition(new StringBuilder().append("(!")
                .append(this.condition)
                .append(")"));
    }

    @Override
    public Condition copy() {
        return new StringCondition(this.condition);
    }
}
