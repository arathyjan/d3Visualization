package com.barclays.ivr.viz.domain.elements;

public class StartOverElement implements IvrElement {

    public StartOverElement() {
    }

    public void accept(IvrElementVisitor visitor) {
        visitor.visit(this);
    }
}
