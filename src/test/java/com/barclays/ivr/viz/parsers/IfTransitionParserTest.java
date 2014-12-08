package com.barclays.ivr.viz.parsers;

import com.barclays.ivr.viz.domain.Condition;
import com.barclays.ivr.viz.domain.Transition;
import com.barclays.ivr.viz.utils.Xml;
import org.jdom2.Element;
import org.junit.Test;

import static com.barclays.ivr.viz.domain.Condition.Type.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class IfTransitionParserTest {

    @Test
    public void shouldParseSimpleIf() throws Exception {
        final Element element = Xml.toXmlElement(
                "<IF result=\"Test\">" +
                        "    <call state=\"State2\"/>" +
                        "</IF>");
        final IfTransitionParser parser = new IfTransitionParser("State1", element);
        final ParseOutput output = parser.parse();

        assertThat(output.transitions, contains(new Transition("State1", "State2", RESULT.create("Test"))));
        assertThat(output.trailingStates(), contains("State2"));
    }

    @Test
    public void shouldParseIfWithMultipleCalls() throws Exception {
        final Element element = Xml.toXmlElement(
                "<IF result=\"Test\">" +
                        "    <call state=\"State2\"/>" +
                        "    <call state=\"State3\"/>" +
                        "    <call state=\"State4\"/>" +
                        "</IF>");
        final IfTransitionParser parser = new IfTransitionParser("State1", element);
        final ParseOutput output = parser.parse();

        assertThat(output.transitions, contains(
                new Transition("State1", "State2", RESULT.create("Test")),
                new Transition("State2", "State3"),
                new Transition("State3", "State4")
        ));
        assertThat(output.trailingStates(), contains("State4"));
    }

    @Test
    public void shouldParseIfWithElse() throws Exception {
        final Element element = Xml.toXmlElement(
                "<IF result=\"Test1\">" +
                        "    <call state=\"State2\"/>" +
                        "    <ELSE result=\"Test2\"/>" +
                        "    <call state=\"State3\"/>" +
                        "</IF>");

        final IfTransitionParser parser = new IfTransitionParser("State1", element);
        final ParseOutput output = parser.parse();

        assertThat(output.transitions, contains(
                new Transition("State1", "State2", RESULT.create("Test1")),
                new Transition("State1", "State3", Condition.Type.ELSE.create())
        ));
        assertThat(output.trailingStates(), contains("State2", "State3"));
    }

    @Test
    public void shouldParseIfWithElif() throws Exception {
        final Element element = Xml.toXmlElement(
                "<IF result=\"Test1\">" +
                        "    <call state=\"State2\"/>" +
                        "    <ELIF result=\"Test2\"/>" +
                        "    <call state=\"State3\"/>" +
                        "</IF>");

        final IfTransitionParser parser = new IfTransitionParser("State1", element);
        final ParseOutput output = parser.parse();

        assertThat(output.transitions, contains(
                new Transition("State1", "State2", RESULT.create("Test1")),
                new Transition("State1", "State3", RESULT.create("Test2"))
        ));
        assertThat(output.trailingStates(), contains("State2", "State3"));
    }

    @Test
    public void shouldParseIfWithMultipleCallsAfterElif() throws Exception {
        final Element element = Xml.toXmlElement(
                "<IF result=\"Test1\">" +
                        "    <call state=\"State2\"/>" +
                        "    <ELIF result=\"Test2\"/>" +
                        "    <call state=\"State3\"/>" +
                        "    <call state=\"State4\"/>" +
                        "    <call state=\"State5\"/>" +
                        "</IF>");

        final IfTransitionParser parser = new IfTransitionParser("State1", element);
        final ParseOutput output = parser.parse();

        assertThat(output.transitions, contains(
                new Transition("State1", "State2", RESULT.create("Test1")),
                new Transition("State1", "State3", RESULT.create("Test2")),
                new Transition("State3", "State4"),
                new Transition("State4", "State5")
        ));
        assertThat(output.trailingStates(), contains("State2", "State5"));
    }

    @Test
    public void shouldParseIfWithMultipleElif() throws Exception {
        final Element element = Xml.toXmlElement(
                "<IF result=\"Test1\">" +
                        "    <call state=\"State2\"/>" +
                        "    <ELIF result=\"Test2\"/>" +
                        "    <call state=\"State3\"/>" +
                        "    <ELIF result=\"Test3\"/>" +
                        "    <call state=\"State4\"/>" +
                        "</IF>");

        final IfTransitionParser parser = new IfTransitionParser("State1", element);

        assertThat(parser.parse().transitions, contains(
                new Transition("State1", "State2", RESULT.create("Test1")),
                new Transition("State1", "State3", RESULT.create("Test2")),
                new Transition("State1", "State4", RESULT.create("Test3"))
        ));
    }

    @Test
    public void shouldParseIfWithGoTo() throws Exception {
        final Element element = Xml.toXmlElement(
                "        <IF result=\"Test1\">" +
                        "    <goto state=\"State2\"/>" +
                        "</IF>");

        final IfTransitionParser parser = new IfTransitionParser("State1", element);
        final ParseOutput output = parser.parse();

        assertThat(output.transitions, contains(
                new Transition("State1", "State2", RESULT.create("Test1"))
        ));
        assertThat(output.trailingCalls, is(empty()));
    }

    @Test
    public void shouldParseIfWithStartOver() throws Exception {
        final Element element = Xml.toXmlElement(
                "        <IF result=\"Test1\">" +
                        "    <call state=\"State2\"/>" +
                        "    <startStateOver/>" +
                        "</IF>");

        final IfTransitionParser parser = new IfTransitionParser("State1", element);
        final ParseOutput output = parser.parse();

        assertThat(output.transitions, contains(
                new Transition("State1", "State2", RESULT.create("Test1")),
                new Transition("State2", "State1")
        ));
        assertThat(output.trailingCalls, is(empty()));
    }

    @Test
    public void shouldParseNestedIf() throws Exception {
        final Element element = Xml.toXmlElement(
                "                <IF true=\"Test1\">" +
                        "                    <call state=\"State2\"/>" +
                        "                    <IF result=\"Test2\">" +
                        "                        <call state=\"State3\"/>" +
                        "                        <call state=\"State4\"/>" +
                        "                    </IF>" +
                        "                    <ELSE/>" +
                        "                    <call state=\"State5\"/>" +
                        "                </IF>");

        final IfTransitionParser parser = new IfTransitionParser("State1", element);
        final ParseOutput output = parser.parse();

        assertThat(output.transitions, contains(
                new Transition("State1", "State2", TRUE.create("Test1")),
                new Transition("State2", "State3", RESULT.create("Test2")),
                new Transition("State3", "State4"),
                new Transition("State1", "State5", ELSE.create())
        ));

        assertThat(output.trailingStates(), contains("State2", "State4", "State5"));
    }

    @Test
    public void shouldParseNestedIfWithTrailingCall() throws Exception {
        final Element element = Xml.toXmlElement(
                "                <IF true=\"Test1\">" +
                        "                    <call state=\"State2\"/>" +
                        "                    <IF result=\"Test2\">" +
                        "                        <call state=\"State3\"/>" +
                        "                        <call state=\"State4\"/>" +
                        "                    </IF>" +
                        "                    <call state=\"State5\"/>" +
                        "                </IF>");

        final IfTransitionParser parser = new IfTransitionParser("State1", element);
        final ParseOutput output = parser.parse();

        assertThat(output.transitions, contains(
                new Transition("State1", "State2", TRUE.create("Test1")),
                new Transition("State2", "State3", RESULT.create("Test2")),
                new Transition("State3", "State4"),
                new Transition("State2", "State5"),
                new Transition("State4", "State5")
        ));
        assertThat(output.trailingStates(), contains("State5"));
    }

    @Test
    public void shouldParseNestedIfsWithNoConditionsInOuterIf() throws Exception {
        final Element element = Xml.toXmlElement(
                "                <IF true=\"Cond1\">" +
                        "                    <IF false=\"Cond2\">" +
                        "                        <IF true=\"Cond3\">" +
                        "                            <call state=\"State2\"/>" +
                        "                        </IF>" +
                        "                    </IF>" +
                        "                    <call state=\"State3\"/>" +
                        "                </IF>");

        final IfTransitionParser parser = new IfTransitionParser("State1", element);
        final ParseOutput output = parser.parse();

        for (Transition transition : output.transitions) {
            System.out.println(transition);
        }
        assertThat(output.transitions, contains(
                new Transition("State1", "State2", TRUE.create("Cond1").and(FALSE.create("Cond2")).and(TRUE.create("Cond3"))),
                new Transition("State1", "State3", TRUE.create("Cond1")),
                new Transition("State2", "State3")));

        assertThat(output.trailingStates(), contains("State3"));
    }
}