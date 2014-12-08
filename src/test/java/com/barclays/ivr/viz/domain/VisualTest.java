package com.barclays.ivr.viz.domain;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class VisualTest {

    private Visual visual;

    @Before
    public void setUp() throws Exception {
        visual = new Visual("/vui.xml");
    }

    @Test
    public void shouldParseAllStates() throws Exception {
        assertThat(visual.states.size(), is(350));
    }

    // Not the greatest tests. Will have to do for today :'(
    @Test
    public void shouldRenderGraphForASingleState() throws Exception {
        final Graph graph = visual.draw("ServiceLineNavigation");

        assertThat(graph.nodes, is(not(empty())));
        assertThat(graph.edges, is(not(empty())));
    }

    @Test
    public void shouldRenderGraphForAllNodes() throws Exception {
        final Graph graph = visual.draw();

        assertThat(graph.nodes, is(not(empty()) ));
        assertThat(graph.edges, is(not(empty())));
    }

    @Test
    public void shouldFindTransitionsRecursivelyFromAState(){
        final Set<String> allTransitions = visual.getAllStatesToVisit("PINLine", Lists.<String>newArrayList());

        assertThat(allTransitions.size(), is(90));
    }
}
