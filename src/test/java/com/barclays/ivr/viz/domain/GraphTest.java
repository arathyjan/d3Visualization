package com.barclays.ivr.viz.domain;

import org.junit.Test;

import static com.barclays.ivr.viz.domain.Condition.Type.TRUE;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class GraphTest {

    @Test
    public void shouldDrawDigraphFromTransitions() throws Exception {
        final Transition transition1 = new Transition("A", "B", TRUE.create("test1"));
        final Transition transition2 = new Transition("B", "C", TRUE.create("test2"));

        Graph graph = new Graph(transition1, transition2);
        final String digraph = graph.toDigraph();

        assertThat(digraph, is("digraph G {\n" +
                        "A -> B [label=\"test1\"];\n" +
                        "B -> C [label=\"test2\"];\n" +
                        "}"
        ));
    }
}