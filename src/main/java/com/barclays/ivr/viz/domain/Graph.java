package com.barclays.ivr.viz.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.base.Joiner;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static java.util.Arrays.asList;

public class Graph {

    private static final ObjectMapper SERIALIZER = new ObjectMapper()
            .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

    public static final Function<Transition, Iterable<Node>> TO_NODE = new Function<Transition, Iterable<Node>>() {
        @Override
        public Iterable<Node> apply(Transition input) {
            return newArrayList(Node.from(input.from), Node.from(input.to));
        }
    };

    public final List<Node> nodes;
    public final List<Edge> edges;
    private final Set<Transition> transitions;

    public Graph(Set<Transition> transitions) {
        this.transitions = transitions;
        nodes = newArrayList(from(transitions).transformAndConcat(TO_NODE).toSet());
        edges = from(transitions).transform(toNode()).toList();
    }

    public Graph(Transition... transitions) {
        this(newLinkedHashSet(asList(transitions)));
    }

    private Function<Transition, Edge> toNode() {
        return new Function<Transition, Edge>() {
            @Override
            public Edge apply(Transition input) {
                final int sourceIndex = nodes.indexOf(Node.from(input.from));
                final int targetIndex = nodes.indexOf(Node.from(input.to));
                return new Edge(sourceIndex, targetIndex, input.condition);
            }
        };
    }

    @Override
    public String toString() {
        try {
            return SERIALIZER.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to print as JSON");
        }
    }

    public String toDigraph() {
        StringBuilder out = new StringBuilder("digraph G {\n");
        Joiner.on("\n").appendTo(out, transitions);
        out.append("\n}");
        return out.toString();
    }
}
