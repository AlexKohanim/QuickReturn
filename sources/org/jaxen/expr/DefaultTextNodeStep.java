package org.jaxen.expr;

import org.jaxen.ContextSupport;
import org.jaxen.expr.iter.IterableAxis;

public class DefaultTextNodeStep extends DefaultStep implements TextNodeStep {
    private static final long serialVersionUID = -3821960984972022948L;

    public DefaultTextNodeStep(IterableAxis axis, PredicateSet predicateSet) {
        super(axis, predicateSet);
    }

    public boolean matches(Object node, ContextSupport support) {
        return support.getNavigator().isText(node);
    }

    public String getText() {
        return getAxisName() + "::text()" + super.getText();
    }
}
