package com.barclays.ivr.viz.domain;

import com.barclays.ivr.viz.parsers.StateParser;
import com.barclays.ivr.viz.utils.Xml;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import org.jdom2.Content;
import org.jdom2.Element;
import org.jdom2.filter.ElementFilter;
import org.jdom2.filter.Filter;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

public class Visual {

    public static final Filter<? extends Content> STATE_FILTER = new ElementFilter("state")
            .or(new ElementFilter("flowState").or(new ElementFilter("processState")));
    public static final Function<State, String> BY_NAME = new Function<State, String>() {
        @Override
        public String apply(State input) {
            return input.name;
        }
    };
    public static final Function<State, Iterable<Transition>> TRANSITIONS = new Function<State, Iterable<Transition>>() {
        @Override
        public Iterable<Transition> apply(State input) {
            return newHashSet(input.transitions);
        }
    };
    public final Function<String, Iterable<Transition>> STATES_TO_TRANSITIONS = new Function<String, Iterable<Transition>>() {
        @Override
        public Iterable<Transition> apply(String input) {
            State state = states.get(input);
            if (state == null) return Collections.emptySet();
            return newHashSet(state.transitions);
        }
    };
    public final Map<String, State> states;
    public static final StateParser PARSER = new StateParser();
    public static final Function<Content, State> TO_STATE = new Function<Content, State>() {
        @Override
        public State apply(Content input) {
            return PARSER.parse((Element) input);
        }
    };


    public Visual(String vuiXmlResource) {
        final Element root = Xml.toXmlElement(getClass().getResourceAsStream(vuiXmlResource));
        final Iterable<? extends Content> elements = root.getDescendants(STATE_FILTER);
        states = from(elements).transform(TO_STATE).uniqueIndex(BY_NAME);
    }

    public Graph draw() {
        final Set<Transition> transitions = from(states.values()).transformAndConcat(TRANSITIONS).toSet();
        return new Graph(transitions);
    }

    public Graph draw(String stateName) {
        final State state = states.get(stateName);
        final Set<Transition> transitions = from(state.transitions)
                .transformAndConcat(new Function<Transition, Iterable<String>>() {
                    @Override
                    public Iterable<String> apply(Transition input) {
                        return newArrayList(input.from, input.to);
                    }
                }).transformAndConcat(new Function<String, Iterable<Transition>>() {
                    @Override
                    public Iterable<Transition> apply(String input) {
                        return states.get(input).transitions;
                    }
                }).toSet();
        return new Graph(transitions);
    }

    public Graph drawRecursively(String stateName) {
        List<String> visitedStates = newArrayList();
        Set<String> allStatesToVisit = getAllStatesToVisit(stateName, visitedStates);
        if (allStatesToVisit.isEmpty())
            return new Graph(Collections.<Transition>emptySet());

        allStatesToVisit.add(stateName);
        final ImmutableSet<Transition> transitions = from(allStatesToVisit).transformAndConcat(STATES_TO_TRANSITIONS).toSet();
        return new Graph(transitions);
    }

    public Set<String> getAllStatesToVisit(String stateName, final List<String> visitedStates) {
        final State state = states.get(stateName);
        if (state == null)
            return ImmutableSet.of();

        final Set<Transition> transitions = state.transitions;
        if (visitedStates.contains(stateName) || transitions.size() == 0)
            return ImmutableSet.of();

        final ImmutableSet<String> level1States = from(transitions).transform(new Function<Transition, String>() {
            @Override
            public String apply(Transition transition) {
                return transition.to;
            }
        }).toSet();
        visitedStates.add(stateName);

        return Sets.newHashSet(Iterables.concat(level1States, from(transitions).transformAndConcat(new Function<Transition, Iterable<String>>() {
            @Override
            public Iterable<String> apply(Transition transition) {
                return getAllStatesToVisit(transition.to, visitedStates);
            }
        })));
    }
}
