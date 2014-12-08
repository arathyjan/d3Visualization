package com.barclays.ivr.viz.domain.elements;

public interface IvrElementVisitor {

    void visit(IfElement element);

    void visit(ElifElement element);

    void visit(ElseElement element);

    void visit(GoToElement element);

    void visit(CallElement element);

    void visit(StartOverElement element);

}
