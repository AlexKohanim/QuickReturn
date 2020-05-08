package org.jaxen.util;

import java.util.Iterator;
import org.jaxen.Navigator;

public class DescendantOrSelfAxisIterator extends DescendantAxisIterator {
    public DescendantOrSelfAxisIterator(Object contextNode, Navigator navigator) {
        super(navigator, (Iterator) new SingleObjectIterator(contextNode));
    }
}
