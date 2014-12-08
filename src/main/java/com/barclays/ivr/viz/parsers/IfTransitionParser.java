package com.barclays.ivr.viz.parsers;

import com.barclays.ivr.viz.domain.Condition;
import com.barclays.ivr.viz.domain.Transition;
import com.barclays.ivr.viz.domain.elements.*;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import org.jdom2.Element;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static com.barclays.ivr.viz.domain.elements.IvrElement.BY_TYPE;
import static com.barclays.ivr.viz.domain.elements.IvrElement.TO_IVR_ELEMENT;
import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Sets.newLinkedHashSet;

public class IfTransitionParser implements IvrElementVisitor {

    public static final Function<Call, String> TO_STATE_NAME = new Function<Call, String>() {
        @Override
        public String apply(Call input) {
            return input.state;
        }
    };
    private final String stateName;
    private final boolean nested;
    private Condition condition;
    private final ImmutableList<IvrElement> children;
    private boolean fromIf;
    private boolean fromCall;
    private boolean fromElse;

    private final Set<Call> initialStates;
    private final LinkedList<Call> trailingCalls;
    private final Set<Transition> transitions;

    public IfTransitionParser(String stateName, Element ifElement, boolean nested, List<Call> initialStates) {
        this(stateName, ifElement, nested, initialStates, new Call(stateName));
    }

    private IfTransitionParser(String stateName, Element ifElement, boolean nested, List<Call> initialStates, Condition trailingCondition) {
        this(stateName, ifElement, nested, initialStates, new Call(stateName, trailingCondition));
    }

    private IfTransitionParser(String stateName, Element ifElement, boolean nested, List<Call> initialStates,
                               Call trailingCall) {
        this.stateName = stateName;
        this.initialStates = newLinkedHashSet(initialStates);
        this.nested = nested;
        final Condition myCondition = Condition.Type.from(ifElement.getAttributes().get(0));
        condition = trailingCall.condition.and(myCondition);
        children = from(ifElement.getChildren()).filter(BY_TYPE).transform(TO_IVR_ELEMENT).toList();
        this.transitions = newLinkedHashSet();
        this.trailingCalls = newLinkedList();
        this.trailingCalls.add(trailingCall);
    }

    public IfTransitionParser(String stateName, Element ifElement) {
        this(stateName, ifElement, false, newArrayList(new Call(stateName)));
    }

    public ParseOutput parse() {
        for (IvrElement child : children) {
            child.accept(this);
        }
        return new ParseOutput(transitions, trailingCalls);
    }

    @Override
    public void visit(IfElement element) {
        ParseOutput output = new IfTransitionParser(stateName, element.element, true, trailingCalls, condition).parse();
        transitions.addAll(output.transitions);
        final List<Call> outputCalls = output.trailingCalls;
        List<Call> duplicates = from(trailingCalls).filter(filter(outputCalls)).toList();
        trailingCalls.removeAll(duplicates);
        trailingCalls.addAll(outputCalls);
        fromCall = false;
        fromIf = true;
        fromElse = false;
    }

    private Predicate<Call> filter(final List<Call> outputCalls) {
        return new Predicate<Call>() {
            @Override
            public boolean apply(Call input) {
                return from(outputCalls).transform(TO_STATE_NAME).contains(input.state);
            }
        };
    }

    @Override
    public void visit(ElifElement element) {
        condition = element.condition;
        fromCall = false;
        fromIf = false;
        fromElse = true;
    }

    @Override
    public void visit(ElseElement element) {
        condition = Condition.Type.ELSE.create();
        fromCall = false;
        fromIf = false;
        fromElse = true;
    }

    @Override
    public void visit(GoToElement element) {
        if (fromCall) {
            transitions.add(new Transition(trailingCalls.getLast().state, element.toState, condition));
        } else {
            transitions.addAll(from(initialStates).transform(toTransition(element.toState, condition)).toList());
            if (!trailingCalls.isEmpty()) trailingCalls.removeLast();
        }
        fromCall = false;
        fromIf = false;
        fromElse = false;
    }

    @Override
    public void visit(final CallElement element) {
        final String toState = element.toState;
        if (fromIf) {
            transitions.addAll(from(trailingCalls).transform(toTransition(toState)).toList());
            trailingCalls.clear();
        } else if (fromCall) {
            Call previous = trailingCalls.getLast();
            transitions.add(new Transition(previous.state, toState, condition));
            trailingCalls.removeLast();
        } else if (fromElse) {
            transitions.addAll(from(initialStates).transform(toTransition(toState, condition)).toList());
        } else {
            transitions.addAll(from(initialStates).transform(toTransition(toState, condition)).toList());
            trailingCalls.removeLast();
        }
        trailingCalls.add(new Call(toState, Condition.NONE));
        condition = Condition.NONE;
        fromCall = true;
        fromIf = false;
        fromElse = false;
    }

    @Override
    public void visit(StartOverElement element) {
        if (nested) {
            transitions.addAll(from(initialStates).transform(toTransition(stateName, condition)).toList());
        } else {
            transitions.add(new Transition(trailingCalls.getLast().state, stateName, condition));
        }
        trailingCalls.clear();
        fromCall = false;
        fromIf = false;
        fromElse = false;
    }

    private Function<Call, Transition> toTransition(final String toState) {
        return new Function<Call, Transition>() {
            @Override
            public Transition apply(Call fromCall) {
                return new Transition(fromCall.state, toState, fromCall.condition);
            }
        };
    }

    private Function<Call, Transition> toTransition(final String toState, final Condition aCondition) {
        return new Function<Call, Transition>() {
            @Override
            public Transition apply(Call fromCall) {
                return new Transition(fromCall.state, toState, aCondition);
            }
        };
    }
}
