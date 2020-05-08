package org.jaxen.expr.iter;

import java.util.Iterator;
import org.jaxen.ContextSupport;
import org.jaxen.UnsupportedAxisException;

public class IterableAncestorAxis extends IterableAxis {
    private static final long serialVersionUID = 1;

    public IterableAncestorAxis(int value) {
        super(value);
    }

    public Iterator iterator(Object contextNode, ContextSupport support) throws UnsupportedAxisException {
        return support.getNavigator().getAncestorAxisIterator(contextNode);
    }
}
