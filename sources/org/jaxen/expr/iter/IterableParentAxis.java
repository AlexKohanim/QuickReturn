package org.jaxen.expr.iter;

import java.util.Iterator;
import org.jaxen.ContextSupport;
import org.jaxen.UnsupportedAxisException;

public class IterableParentAxis extends IterableAxis {
    private static final long serialVersionUID = -7521574185875636490L;

    public IterableParentAxis(int value) {
        super(value);
    }

    public Iterator iterator(Object contextNode, ContextSupport support) throws UnsupportedAxisException {
        return support.getNavigator().getParentAxisIterator(contextNode);
    }
}
