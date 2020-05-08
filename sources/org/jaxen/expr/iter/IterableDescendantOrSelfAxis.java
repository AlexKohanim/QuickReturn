package org.jaxen.expr.iter;

import java.util.Iterator;
import org.jaxen.ContextSupport;
import org.jaxen.UnsupportedAxisException;

public class IterableDescendantOrSelfAxis extends IterableAxis {
    private static final long serialVersionUID = 2956703237251023850L;

    public IterableDescendantOrSelfAxis(int value) {
        super(value);
    }

    public Iterator iterator(Object contextNode, ContextSupport support) throws UnsupportedAxisException {
        return support.getNavigator().getDescendantOrSelfAxisIterator(contextNode);
    }
}
