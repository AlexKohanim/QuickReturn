package org.jaxen.util;

import org.jaxen.Navigator;

public class AncestorAxisIterator extends AncestorOrSelfAxisIterator {
    public AncestorAxisIterator(Object contextNode, Navigator navigator) {
        super(contextNode, navigator);
        next();
    }
}
