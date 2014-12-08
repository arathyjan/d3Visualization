package com.barclays.ivr.viz.parsers;

import com.barclays.ivr.viz.domain.Condition;

public class Call {
    public final String state;
    public final Condition condition;

    public Call(String state) {
        this(state, Condition.NONE);
    }

    public Call(String state, Condition condition) {
        this.state = state;
        this.condition = condition;
    }

    @Override
    public String toString() {
        return state + ", condition = " + condition;
    }
}
