package org.creativelabs.graph.edge.condition;

/**
 * @author azotcsit
 *         Date: 08.04.11
 *         Time: 22:21
 */
public class StringEdgeCondition implements EdgeCondition {

    private StringBuilder condition;

    public StringEdgeCondition(String condition) {
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
    public EdgeCondition and(EdgeCondition condition) {
        if (!(condition instanceof EmptyEdgeCondition)) {
            this.condition = new StringBuilder().append("(")
                    .append(this.condition)
                    .append("&&")
                    .append(condition.getStringRepresentation())
                    .append(")");
        }
        return this;
    }

    @Override
    public EdgeCondition or(EdgeCondition condition) {
        if (!(condition instanceof EmptyEdgeCondition)) {
            this.condition = new StringBuilder().append("(")
                    .append(this.condition)
                    .append("||")
                    .append(condition.getStringRepresentation())
                    .append(")");
        }
        return this;
    }

    @Override
    public EdgeCondition not() {
        this.condition = new StringBuilder().append("(!")
                .append(this.condition)
                .append(")");
        return this;
    }
}
