package com.barclays.ivr.viz.domain;

public class Edge {

    public final int source;
    public final int target;
    public final String condition;
    public final int index;
    private static int COUNTER = 0;

    public Edge(int source, int target, Condition condition) {
        this.source = source;
        this.target = target;
        this.condition = condition.toString();
        this.index = COUNTER++;
    }
}
