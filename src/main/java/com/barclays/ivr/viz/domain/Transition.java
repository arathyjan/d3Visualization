package com.barclays.ivr.viz.domain;

public class Transition {
    public final Condition condition;
    public final String from;
    public final String to;

    public Transition(String from, String to, Condition condition) {
        this.from = from;
        this.to = to;
        this.condition = condition;
    }

    public Transition(String from, String to) {
        this(from, to, Condition.NONE);
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append(from).append(" -> ").append(to);
        if (conditional()) out.append(" [label=\"").append(condition).append("\"];");
        return out.toString();
    }

    private boolean conditional() {
        return !condition.equals(Condition.NONE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Transition that = (Transition) o;

        return from.equals(that.from) && to.equals(that.to) && condition.equals(that.condition);
    }

    @Override
    public int hashCode() {
        int result = condition.hashCode();
        result = 31 * result + from.hashCode();
        result = 31 * result + to.hashCode();
        return result;
    }
}
