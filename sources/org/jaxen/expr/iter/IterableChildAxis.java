package org.jaxen.expr.iter;

import java.util.Iterator;
import org.jaxen.ContextSupport;
import org.jaxen.NamedAccessNavigator;
import org.jaxen.UnsupportedAxisException;

public class IterableChildAxis extends IterableAxis {
    private static final long serialVersionUID = 1;

    public IterableChildAxis(int value) {
        super(value);
    }

    public Iterator iterator(Object contextNode, ContextSupport support) throws UnsupportedAxisException {
        return support.getNavigator().getChildAxisIterator(contextNode);
    }

    public Iterator namedAccessIterator(Object contextNode, ContextSupport support, String localName, String namespacePrefix, String namespaceURI) throws UnsupportedAxisException {
        return ((NamedAccessNavigator) support.getNavigator()).getChildAxisIterator(contextNode, localName, namespacePrefix, namespaceURI);
    }

    public boolean supportsNamedAccess(ContextSupport support) {
        return support.getNavigator() instanceof NamedAccessNavigator;
    }
}
