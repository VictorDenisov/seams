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

    @Override
    public String getStringRepresentation() {
        return condition.toString();
    }

    @Override
    public Condition and(Condition condition) {
        if (!(condition instanceof EmptyCondition)) {
            this.condition = new StringBuilder().append("(")
                    .append(this.condition)
                    .append("&&")
                    .append(condition.getStringRepresentation())
                    .append(")");
        }
        return this;
    }

    @Override
    public Condition or(Condition condition) {
        if (!(condition instanceof EmptyCondition)) {
            this.condition = new StringBuilder().append("(")
                    .append(this.condition)
                    .append("||")
                    .append(condition.getStringRepresentation())
                    .append(")");
        }
        return this;
    }

    @Override
    public Condition not() {
        this.condition = new StringBuilder().append("(!")
                .append(this.condition)
                .append(")");
        return this;
    }
}
