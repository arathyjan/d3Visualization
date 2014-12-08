package com.barclays.ivr.viz.domain;

import org.jdom2.Attribute;

public class Condition {

    public Condition and(Condition another) {
        if (this == NONE) {
            return another;
        }
        if (another == NONE) {
            return this;
        }
        return new Condition(Type.AND, toString() + " and " + another.toString());
    }

    public static enum Type {

        TRUE("", ""),
        FALSE("", " is not true"),
        RESULT("result = ", ""),
        ELSE("else", ""),
        COND("", ""),
        UNAVAILABLE("", " is unavailable"),
        NONE("", ""),
        AND("", "");
        private final String prefix;
        private final String suffix;

        Type(String prefix, String suffix) {
            this.prefix = prefix;
            this.suffix = suffix;
        }

        public static Condition from(Attribute attribute) {
            final Type type = Type.valueOf(attribute.getName().toUpperCase());
            return type.create(attribute.getValue());
        }

        public Condition create(String expression) {
            return new Condition(this, expression);
        }

        public Condition create() {
            return create("");
        }

        public String toString(String expression) {
            return prefix + expression + suffix;
        }

    }

    public static final Condition NONE = new Condition(Type.NONE, "");

    private final Type type;
    private final String expression;

    private Condition(Type type, String expression) {
        this.type = type;
        this.expression = expression.replaceAll("([A-Z][a-z])", " $1").toLowerCase().trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Condition condition = (Condition) o;
        return type == condition.type && expression.equals(condition.expression);
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + expression.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return type.toString(expression);
    }

}
