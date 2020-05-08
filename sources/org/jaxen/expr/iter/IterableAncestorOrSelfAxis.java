package org.jaxen.expr.iter;

import java.util.Iterator;
import org.jaxen.ContextSupport;
import org.jaxen.UnsupportedAxisException;

public class IterableAncestorOrSelfAxis extends IterableAxis {
    private static final long serialVersionUID = 1;

    public IterableAncestorOrSelfAxis(int value) {
        super(value);
    }

    public Iterator iterator(Object contextNode, ContextSupport support) throws UnsupportedAxisException {
        return support.getNavigator().getAncestorOrSelfAxisIterator(contextNode);
    }
}
