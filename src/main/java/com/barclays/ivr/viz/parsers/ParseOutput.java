package com.barclays.ivr.viz.parsers;

import com.barclays.ivr.viz.domain.Transition;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

import java.util.List;
import java.util.Set;

public class ParseOutput {
    public final Set<Transition> transitions;
    public final List<Call> trailingCalls;

    public ParseOutput(Set<Transition> transitions, List<Call> trailingCalls) {
        this.transitions = transitions;
        this.trailingCalls = trailingCalls;
    }

    public Set<String> trailingStates() {
        return FluentIterable.from(trailingCalls).transform(new Function<Call, String>() {
            @Override
            public String apply(Call input) {
                return input.state;
            }
        }).toSet();
    }
}
