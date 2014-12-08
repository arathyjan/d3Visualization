package com.barclays.ivr.viz.domain;

import com.google.common.base.Function;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

public class State {
    public static final Function<Transition, Iterable<String>> TRANSITION_STATES = new Function<Transition, Iterable<String>>() {
        @Override
        public Iterable<String> apply(Transition input) {
            return newHashSet(input.from, input.to);
        }
    };
    public final String name;
    public final Set<Transition> transitions;

    public State(String name, Set<Transition> transitions) {
        this.name = name;
        this.transitions = transitions;
    }

    @Override
    public String toString() {
        final StringBuilder out = new StringBuilder("Name: ").append(name).append("\n");
        for (Transition transition : transitions) {
            out.append(transition).append("\n");
        }
        return out.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        State state = (State) o;

        return name.equals(state.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

}
