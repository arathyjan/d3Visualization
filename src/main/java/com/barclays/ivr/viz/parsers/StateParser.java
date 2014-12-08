package com.barclays.ivr.viz.parsers;

import com.barclays.ivr.viz.domain.State;
import com.barclays.ivr.viz.domain.Transition;
import com.barclays.ivr.viz.utils.Constants;
import com.google.common.base.Function;
import org.jdom2.Element;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newLinkedHashSet;

// TODO: Convert to use the visitor
public class StateParser {

    public State parse(Element stateElement) {
        String stateName = stateElement.getChild(Constants.name).getValue().trim();

        if (stateElement.getName().equals("state")) {
            return new State(stateName, Collections.<Transition>emptySet());
        }
        final List<Element> actions = getElements(stateElement);
        Set<Transition> transitions = newLinkedHashSet();
        Set<Call> fromCalls = newLinkedHashSet();
        fromCalls.add(new Call(stateName));
        boolean fromCall = false;
        boolean fromIf = false;

        for (final Element action : actions) {
            final String actionName = action.getName();
            if (actionName.equals("call")) {
                final String to = action.getAttributeValue("state");
                transitions.addAll(from(fromCalls).transform(toTransition(to)).toList());
                fromCalls.clear();
                fromCalls.add(new Call(to));
                fromCall = true;
                fromIf = false;
            } else if (actionName.equals("goto")) {
                final String to = action.getAttributeValue("state");
                transitions.addAll(from(fromCalls).transform(toTransition(to)).toList());
                fromCall = false;
                fromIf = false;
            } else if (actionName.equals("IF")) {
                boolean nested = fromCall || fromIf;
                final IfTransitionParser parser = new IfTransitionParser(stateName, action, nested, newArrayList(fromCalls));
                final ParseOutput output = parser.parse();
                final Set<Transition> newTransitions = output.transitions;
                fromCalls.addAll(output.trailingCalls);
                transitions.addAll(newTransitions);
                fromCall = false;
                fromIf = true;
            }
        }
        return new State(stateName, transitions);
    }

    private Function<Call, Transition> toTransition(final String to) {
        return new Function<Call, Transition>() {
            @Override
            public Transition apply(Call from) {
                return new Transition(from.state, to);
            }
        };
    }

    private List<Element> getElements(Element stateElement) {
        String name = stateElement.getName();
        if ("flowState".equals(name))
            return stateElement.getChild("Flow").getChildren();
        return stateElement.getChild("Process").getChildren();
    }
}
