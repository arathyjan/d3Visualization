package com.barclays.ivr.viz.domain.elements;

public class ElseElement implements IvrElement {

    public ElseElement() {
    }

    public void accept(IvrElementVisitor visitor) {
        visitor.visit(this);
    }
}
