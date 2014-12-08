package com.barclays.ivr.viz.parsers;

import com.barclays.ivr.viz.domain.State;
import com.barclays.ivr.viz.domain.Transition;
import com.barclays.ivr.viz.utils.Xml;
import org.jdom2.Element;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static com.barclays.ivr.viz.domain.Condition.Type.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class StateParserTest {

    public static final String FLOW_STATE_XML =
            "    <flowState>" +
                    "        <name>ServiceLineNavigation</name>" +
                    "        <Flow>" +
                    "            <call state=\"MainMenu\"/>" +
                    "            <IF result=\"Touchtone\">" +
                    "                <setFlag true=\"TouchtoneMenus\"/>" +
                    "                <call state=\"MainMenu_TT\"/>" +
                    "            </IF>" +
                    "            <IF result=\"SomethingElse\">" +
                    "                <setFlag true=\"TouchtoneMenus\"/>" +
                    "                <call state=\"SomethingElse\"/>" +
                    "                <IF result=\"Touchtone\">" +
                    "                    <setFlag true=\"TouchtoneMenus\"/>" +
                    "                    <call state=\"MoreOptions_TT\"/>" +
                    "                </IF>" +
                    "                <ELIF result=\"MoreOptions\"/>" +
                    "                <call state=\"MoreOptions_TT\"/>" +
                    "            </IF>" +
                    "            <IF result=\"MainMenu\">" +
                    "                <startStateOver/>" +
                    "            </IF>" +
                    "            <goto state=\"Functions\"/>" +
                    "        </Flow>" +
                    "    </flowState>";
    public static final String PROCESS_STATE_XML =
            "        <processState>" +
                    "            <name>Multicard</name>" +
                    "            <Process>" +
                    "                <IF true=\"LineMatch\">" +
                    "                    <IF true=\"MultipleLineMatches\">" +
                    "                        <call state=\"CardsCheckQuery\"/>" +
                    "                        <IF result=\"Yes\">" +
                    "                            <ELSE/>" +
                    "                            <IF cond=\"NumberNotLineMatches==1\"> <!-- only one other card -->" +
                    "                                <call state=\"CardCheckQuery\"/>" +
                    "                                <IF result=\"Yes\">" +
                    "                                    <return/>" +
                    "                                    <ELSE/>" +
                    "                                    <goto state=\"RepHandling\"/>" +
                    "                                </IF>" +
                    "                                <ELSE/>" +
                    "                            </IF>" +
                    "                        </IF>" +
                    "                        <ELSE/>" +
                    "                        <call state=\"CardCheckQuery\"/>" +
                    "                        <IF result=\"Yes\">" +
                    "                            <return/>" +
                    "                            <IF cond=\"NumberNotLineMatches==1\"> <!-- only one other card -->" +
                    "                                <call state=\"CardCheckQuery\"/>" +
                    "                                <IF result=\"Yes\">" +
                    "                                    <return/>" +
                    "                                    <ELSE/>" +
                    "                                    <goto state=\"RepHandling\"/>" +
                    "                                </IF>" +
                    "                                <ELSE/>" +
                    "                            </IF>" +
                    "                        </IF>" +
                    "                    </IF>" +
                    "                </IF>" +
                    "                <call state=\"MulticardMenus\"/>" +
                    "                <return/>" +
                    "            </Process>" +
                    "        </processState>";

    public static final String PLAIN_STATE_XML =
            "    <state>" +
                    "        <name>RepeatMailingAddressQuery</name>" +
                    "        <tt state=\"RepeatMailingAddressQuery_TT\"/>" +
                    "        <Prompts>" +
                    "            <Prompt>" +
                    "                <prompt text=\"Would you like to hear the mailing address again?\"" +
                    "                        audio=\"RepeatMailingAddressQuery_prompt.wav\"/>" +
                    "            </Prompt>" +
                    "            <NoInput>" +
                    "                <prompt text=\"Please say yes or no.\" audio=\"NoInputYN1.wav\"/>" +
                    "            </NoInput>" +
                    "            <NoInput>" +
                    "                <prompt text=\"Sorry, I didn't hear you.\" audio=\"NoInputA.wav\"/>" +
                    "                <period/>" +
                    "                <reminder text=\"To hear the mailing address again, say Yes; otherwise, say No.\"" +
                    "                          audio=\"RepeatMailingAddressQuery_reminder.wav\"/>" +
                    "            </NoInput>" +
                    "            <NoInput>" +
                    "                <return result=\"No\"/>" +
                    "            </NoInput>" +
                    "            <NoMatch>" +
                    "                <prompt text=\"Was that a yes or a no?\" audio=\"NoMatchYN1.wav\"/>" +
                    "            </NoMatch>" +
                    "            <NoMatch>" +
                    "                <prompt text=\"Sorry, I still didn't understand you.\" audio=\"NoMatchB.wav\"/>" +
                    "                <period/>" +
                    "                <reminder/>" +
                    "            </NoMatch>" +
                    "            <NoMatch>" +
                    "                <return result=\"No\"/>" +
                    "            </NoMatch>" +
                    "            <Help>" +
                    "                <reminder/>" +
                    "            </Help>" +
                    "        </Prompts>" +
                    "        <SpeechActions>" +
                    "            <Action command=\"Yes\">" +
                    "                <return result=\"Yes\"/>" +
                    "            </Action>" +
                    "            <Action command=\"No\">" +
                    "                <return result=\"No\"/>" +
                    "            </Action>" +
                    "            <GlobalAction set=\"90\"/>" +
                    "        </SpeechActions>" +
                    "        <TouchtoneActions>" +
                    "            <Action key=\"1\">" +
                    "                <return result=\"Yes\"/>" +
                    "            </Action>" +
                    "            <Action key=\"2\">" +
                    "                <return result=\"No\"/>" +
                    "            </Action>" +
                    "            <GlobalAction set=\"90\"/>" +
                    "        </TouchtoneActions>" +
                    "    </state>";


    private StateParser stateParser;

    @Before
    public void setUp() throws Exception {
        stateParser = new StateParser();
    }

    @Test
    public void shouldParseFlowState() throws Exception {
        final State state = stateParser.parse(Xml.toXmlElement(FLOW_STATE_XML));

        assertThat(state.name, is("ServiceLineNavigation"));
        assertThat(state.transitions.size(), is(15));

        assertThat(state.transitions, hasItem(new Transition("ServiceLineNavigation", "MainMenu")));

        assertThat(state.transitions, hasItem(new Transition("MainMenu", "MainMenu_TT", RESULT.create("Touchtone"))));
        assertThat(state.transitions, hasItem(new Transition("MainMenu", "SomethingElse", RESULT.create("SomethingElse"))));
        assertThat(state.transitions, hasItem(new Transition("MainMenu", "MoreOptions_TT", RESULT.create("MoreOptions"))));
        assertThat(state.transitions, hasItem(new Transition("MainMenu", "ServiceLineNavigation", RESULT.create("MainMenu"))));
        assertThat(state.transitions, hasItem(new Transition("MainMenu", "Functions")));

        assertThat(state.transitions, hasItem(new Transition("SomethingElse", "MoreOptions_TT", RESULT.create("Touchtone"))));
        assertThat(state.transitions, hasItem(new Transition("SomethingElse", "ServiceLineNavigation", RESULT.create("MainMenu"))));
        assertThat(state.transitions, hasItem(new Transition("SomethingElse", "Functions")));

        assertThat(state.transitions, hasItem(new Transition("MainMenu_TT", "SomethingElse", RESULT.create("SomethingElse"))));
        assertThat(state.transitions, hasItem(new Transition("MainMenu_TT", "MoreOptions_TT", RESULT.create("MoreOptions"))));
        assertThat(state.transitions, hasItem(new Transition("MainMenu_TT", "Functions")));

        assertThat(state.transitions, hasItem(new Transition("MoreOptions_TT", "ServiceLineNavigation", RESULT.create("MainMenu"))));
        assertThat(state.transitions, hasItem(new Transition("MoreOptions_TT", "Functions")));
    }

    @Test
    public void shouldParseProcessState() throws Exception {
        final State state = stateParser.parse(Xml.toXmlElement(PROCESS_STATE_XML));

        assertThat(state.name, is("Multicard"));
        assertThat(state.transitions.size(), is(8));
    }

    @Test
    public void shouldParsePlainState() throws Exception {
        final Element element = Xml.toXmlElement(PLAIN_STATE_XML);
        final State state = stateParser.parse(element);

        assertThat(state.name, is("RepeatMailingAddressQuery"));
        assertThat(state.transitions.size(), is(0));
    }

    @Test
    public void shouldParseCallFlow() throws Exception {
        final State state = stateParser.parse(Xml.toXmlElement(
                "<flowState>" +
                        "    <name>Intro</name>" +
                        "    <Flow>" +
                        "       <call state=\"LanguageAndDisclaimer\"/>" +
                        "    </Flow>" +
                        "</flowState>"
        ));
        assertThat(state.transitions, contains(new Transition("Intro", "LanguageAndDisclaimer")));
    }

    @Test
    public void shouldParseGoToFlow() throws Exception {
        final State state = stateParser.parse(Xml.toXmlElement(
                "<flowState>" +
                        "    <name>Intro</name>" +
                        "    <Flow>" +
                        "       <goto state=\"LanguageAndDisclaimer\"/>" +
                        "    </Flow>" +
                        "</flowState>"
        ));
        assertThat(state.transitions, contains(new Transition("Intro", "LanguageAndDisclaimer")));
    }

    @Test
    public void shouldParseFlowWithTrailingCall() throws Exception {
        final State state = stateParser.parse(Xml.toXmlElement(
                "            <flowState>" +
                        "        <name>State1</name>" +
                        "        <Flow>" +
                        "            <call state=\"State2\"/>" +
                        "            <IF result=\"Touchtone\">" +
                        "                <call state=\"State3\"/>" +
                        "                <call state=\"State4\"/>" +
                        "            </IF>" +
                        "            <call state=\"State5\"/>" +
                        "        </Flow>" +
                        "    </flowState>"));

        assertThat(state.transitions.size(), is(5));
    }

    @Test
    public void shouldParseIntroState() throws Exception {
        final State state = stateParser.parse(Xml.toXmlElement(
                "        <flowState>\n" +
                "            <name>Intro</name>\n" +
                "            <Flow>\n" +
                "                <call state=\"WelcomeMessage\"/>\n" +
                "                <IF false=\"TransferredFromRep\">\n" +
                "                    <call state=\"LanguageAndDisclaimer\"/>\n" +
                "                </IF>\n" +
                "                <IF true=\"PaymentLine\">\n" +
                "                    <goto state=\"PaymentLine\"/>\n" +
                "                    <ELIF true=\"PINLine\"/>\n" +
                "                    <goto state=\"PINLine\"/>\n" +
                "                    <ELSE/>\n" +
                "                    <goto state=\"ServiceLine\"/>\n" +
                "                </IF>\n" +
                "            </Flow>\n" +
                "        </flowState>\n"));


        final Set<Transition> transitions = state.transitions;
        assertThat(transitions.size(), is(8));

        assertThat(transitions, hasItem(new Transition("Intro", "WelcomeMessage")));
        assertThat(transitions, hasItem(new Transition("WelcomeMessage", "LanguageAndDisclaimer", FALSE.create("TransferredFromRep"))));
        assertThat(transitions, hasItem(new Transition("WelcomeMessage", "PaymentLine", TRUE.create("PaymentLine"))));
        assertThat(transitions, hasItem(new Transition("WelcomeMessage", "PINLine", TRUE.create("PINLine"))));
        assertThat(transitions, hasItem(new Transition("WelcomeMessage", "ServiceLine", ELSE.create())));
        assertThat(transitions, hasItem(new Transition("LanguageAndDisclaimer", "PaymentLine", TRUE.create("PaymentLine"))));
        assertThat(transitions, hasItem(new Transition("LanguageAndDisclaimer", "PINLine", TRUE.create("PINLine"))));
        assertThat(transitions, hasItem(new Transition("LanguageAndDisclaimer", "ServiceLine", ELSE.create())));
    }
}