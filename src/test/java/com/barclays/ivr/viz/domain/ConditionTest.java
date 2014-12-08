package com.barclays.ivr.viz.domain;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

public class ConditionTest {

    @Test
    public void shouldCreateTrueType() throws Exception {
        final Condition condition = Condition.Type.TRUE.create("TransferFromRep");

        assertThat(condition.toString(), is("transfer from rep"));
    }

    @Test
    public void shouldCreateResultType() throws Exception {
        final Condition condition = Condition.Type.RESULT.create("PINLine");

        assertThat(condition.toString(), is("result = pin line"));
    }

    @Test
    public void shouldCreateFalseType() throws Exception {
        final Condition condition = Condition.Type.FALSE.create("TransferFromRep");

        assertThat(condition.toString(), is("transfer from rep is not true"));
    }

    @Test
    public void shouldCreateCondType() throws Exception {
        final Condition condition = Condition.Type.COND.create("NumberOfRetries==1");

        assertThat(condition.toString(), is("number of retries==1"));
    }

    @Test
    public void shouldCreateElseType() throws Exception {
        final Condition condition = Condition.Type.ELSE.create();

        assertThat(condition.toString(), is("else"));
    }

    @Test
    public void shouldCreateNoneType() throws Exception {
        final Condition condition = Condition.Type.NONE.create();

        assertThat(condition.toString(), is(""));
    }

    @Test
    public void shouldCreateUnavailableType() throws Exception {
        final Condition condition = Condition.Type.UNAVAILABLE.create("PaymentsModel");

        assertThat(condition.toString(), is("payments model is unavailable"));
    }

    @Test
    public void shouldReturnInputConditionIfAndingWithNone() throws Exception {
        final Condition condition1 = Condition.Type.TRUE.create("condition1");

        assertThat(condition1.and(Condition.NONE), is(sameInstance(condition1)));
        assertThat(Condition.NONE.and(condition1), is(sameInstance(condition1)));
    }

    @Test
    public void shouldCreateAndCondition() throws Exception {
        final Condition condition1 = Condition.Type.TRUE.create("condition1");
        final Condition andCondition = condition1.and(Condition.Type.TRUE.create("condition2"));

        assertThat(andCondition.toString(), is("condition1 and condition2"));
    }
}