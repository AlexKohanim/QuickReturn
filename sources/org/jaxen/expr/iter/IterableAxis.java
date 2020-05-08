package org.jaxen.expr.iter;

import java.io.Serializable;
import java.util.Iterator;
import org.jaxen.ContextSupport;
import org.jaxen.UnsupportedAxisException;

public abstract class IterableAxis implements Serializable {
    private int value;

    public abstract Iterator iterator(Object obj, ContextSupport contextSupport) throws UnsupportedAxisException;

    public IterableAxis(int axisValue) {
        this.value = axisValue;
    }

    public int value() {
        return this.value;
    }

    public Iterator namedAccessIterator(Object contextNode, ContextSupport support, String localName, String namespacePrefix, String namespaceURI) throws UnsupportedAxisException {
        throw new UnsupportedOperationException("Named access unsupported");
    }

    public boolean supportsNamedAccess(ContextSupport support) {
        return false;
    }
}
