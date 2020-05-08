package org.jaxen.expr.iter;

import java.util.Iterator;
import org.jaxen.ContextSupport;
import org.jaxen.UnsupportedAxisException;

public class IterableDescendantAxis extends IterableAxis {
    private static final long serialVersionUID = 7286715505909806723L;

    public IterableDescendantAxis(int value) {
        super(value);
    }

    public Iterator iterator(Object contextNode, ContextSupport support) throws UnsupportedAxisException {
        return support.getNavigator().getDescendantAxisIterator(contextNode);
    }
}
