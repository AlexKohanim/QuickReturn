package org.jaxen.expr.iter;

import java.util.Iterator;
import org.jaxen.ContextSupport;
import org.jaxen.UnsupportedAxisException;

public class IterableFollowingAxis extends IterableAxis {
    private static final long serialVersionUID = -7100245752300813209L;

    public IterableFollowingAxis(int value) {
        super(value);
    }

    public Iterator iterator(Object contextNode, ContextSupport support) throws UnsupportedAxisException {
        return support.getNavigator().getFollowingAxisIterator(contextNode);
    }
}
