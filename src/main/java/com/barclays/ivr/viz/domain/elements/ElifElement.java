package com.barclays.ivr.viz.domain.elements;

import com.barclays.ivr.viz.domain.Condition;
import org.jdom2.Element;

public class ElifElement implements IvrElement {
    public final Condition condition;

    public ElifElement(Element element) {
        condition = Condition.Type.from(element.getAttributes().get(0));
    }

    public void accept(IvrElementVisitor visitor) {
        visitor.visit(this);
    }
}
