package com.barclays.ivr.viz.utils;

import org.jdom2.Namespace;

public class Constants {
    public static final String name = "name";
    public static final String callAction = "call";
    public static final String gotoAction = "goto";
    public static final String stateAttribute = "state";
    public static final String appAttribute = "app";
    public static final String IF = "IF";
    public static final String ELSE_IF = "ELIF";
    public static final String ELSE = "ELSE";
    public static final String resultCondition = "result";
    public static final String trueCondition = "true";
    public static final String falseCondition = "false";
    public static final Namespace defaultNs = Namespace.getNamespace("barclays", "http://barclaycardus.com/schemas/vui");

}
