package com.barclays.ivr.viz.domain.elements;

import org.jdom2.Element;

public class CallElement implements IvrElement {
    public final String toState;

    public CallElement(Element element) {
        this.toState = element.getAttributeValue("state");
    }

    public void accept(IvrElementVisitor visitor) {
        visitor.visit(this);
    }
}
