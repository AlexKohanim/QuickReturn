package org.jaxen.expr.iter;

import java.util.Iterator;
import org.jaxen.ContextSupport;
import org.jaxen.UnsupportedAxisException;

public class IterablePrecedingAxis extends IterableAxis {
    private static final long serialVersionUID = 587333938258540052L;

    public IterablePrecedingAxis(int value) {
        super(value);
    }

    public Iterator iterator(Object contextNode, ContextSupport support) throws UnsupportedAxisException {
        return support.getNavigator().getPrecedingAxisIterator(contextNode);
    }
}
