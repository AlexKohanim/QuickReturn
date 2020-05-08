package org.jaxen.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.jaxen.JaxenConstants;
import org.jaxen.Navigator;
import org.jaxen.UnsupportedAxisException;

public class FollowingSiblingAxisIterator implements Iterator {
    private Object contextNode;
    private Navigator navigator;
    private Iterator siblingIter;

    public FollowingSiblingAxisIterator(Object contextNode2, Navigator navigator2) throws UnsupportedAxisException {
        this.contextNode = contextNode2;
        this.navigator = navigator2;
        init();
    }

    private void init() throws UnsupportedAxisException {
        Object parent = this.navigator.getParentNode(this.contextNode);
        if (parent != null) {
            this.siblingIter = this.navigator.getChildAxisIterator(parent);
            while (this.siblingIter.hasNext()) {
                if (this.siblingIter.next().equals(this.contextNode)) {
                    return;
                }
            }
            return;
        }
        this.siblingIter = JaxenConstants.EMPTY_ITERATOR;
    }

    public boolean hasNext() {
        return this.siblingIter.hasNext();
    }

    public Object next() throws NoSuchElementException {
        return this.siblingIter.next();
    }

    public void remove() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}
