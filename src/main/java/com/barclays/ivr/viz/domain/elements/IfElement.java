package com.barclays.ivr.viz.domain.elements;

import org.jdom2.Element;

public class IfElement implements IvrElement {
    public final Element element;

    public IfElement(Element element) {
        this.element = element;
    }

    @Override
    public void accept(IvrElementVisitor visitor) {
        visitor.visit(this);
    }

    public String getState() {
        return element.getAttributeValue("state");
    }
}
