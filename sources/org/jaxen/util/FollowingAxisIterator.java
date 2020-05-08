package org.jaxen.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.jaxen.JaxenConstants;
import org.jaxen.JaxenRuntimeException;
import org.jaxen.Navigator;
import org.jaxen.UnsupportedAxisException;

public class FollowingAxisIterator implements Iterator {
    private Object contextNode;
    private Iterator currentSibling = JaxenConstants.EMPTY_ITERATOR;
    private Navigator navigator;
    private Iterator siblings;

    public FollowingAxisIterator(Object contextNode2, Navigator navigator2) throws UnsupportedAxisException {
        this.contextNode = contextNode2;
        this.navigator = navigator2;
        this.siblings = navigator2.getFollowingSiblingAxisIterator(contextNode2);
    }

    private boolean goForward() {
        while (!this.siblings.hasNext()) {
            if (!goUp()) {
                return false;
            }
        }
        this.currentSibling = new DescendantOrSelfAxisIterator(this.siblings.next(), this.navigator);
        return true;
    }

    private boolean goUp() {
        if (this.contextNode == null || this.navigator.isDocument(this.contextNode)) {
            return false;
        }
        try {
            this.contextNode = this.navigator.getParentNode(this.contextNode);
            if (this.contextNode == null || this.navigator.isDocument(this.contextNode)) {
                return false;
            }
            this.siblings = this.navigator.getFollowingSiblingAxisIterator(this.contextNode);
            return true;
        } catch (UnsupportedAxisException e) {
            throw new JaxenRuntimeException((Throwable) e);
        }
    }

    public boolean hasNext() {
        while (!this.currentSibling.hasNext()) {
            if (!goForward()) {
                return false;
            }
        }
        return true;
    }

    public Object next() throws NoSuchElementException {
        if (hasNext()) {
            return this.currentSibling.next();
        }
        throw new NoSuchElementException();
    }

    public void remove() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}
