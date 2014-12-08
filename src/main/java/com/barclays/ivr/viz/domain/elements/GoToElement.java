package com.barclays.ivr.viz.domain.elements;

import org.jdom2.Element;

public class GoToElement implements IvrElement {
    public final String toState;

    public GoToElement(Element element) {
        this.toState = element.getAttributes().get(0).getValue();
    }

    @Override
    public void accept(IvrElementVisitor visitor) {
        visitor.visit(this);
    }
}
