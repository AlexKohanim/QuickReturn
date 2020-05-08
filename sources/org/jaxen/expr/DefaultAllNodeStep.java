package org.jaxen.expr;

import org.jaxen.ContextSupport;
import org.jaxen.expr.iter.IterableAxis;

public class DefaultAllNodeStep extends DefaultStep implements AllNodeStep {
    private static final long serialVersionUID = 292886316770123856L;

    public DefaultAllNodeStep(IterableAxis axis, PredicateSet predicateSet) {
        super(axis, predicateSet);
    }

    public String toString() {
        return "[(DefaultAllNodeStep): " + getAxisName() + "]";
    }

    public String getText() {
        return getAxisName() + "::node()" + super.getText();
    }

    public boolean matches(Object node, ContextSupport contextSupport) {
        return true;
    }
}
