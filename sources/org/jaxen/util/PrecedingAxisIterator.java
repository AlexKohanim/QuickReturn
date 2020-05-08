package org.jaxen.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import org.jaxen.JaxenConstants;
import org.jaxen.JaxenRuntimeException;
import org.jaxen.Navigator;
import org.jaxen.UnsupportedAxisException;

public class PrecedingAxisIterator implements Iterator {
    private Iterator ancestorOrSelf;
    private ListIterator childrenOrSelf = JaxenConstants.EMPTY_LIST_ITERATOR;
    private Navigator navigator;
    private Iterator precedingSibling = JaxenConstants.EMPTY_ITERATOR;
    private ArrayList stack = new ArrayList();

    public PrecedingAxisIterator(Object contextNode, Navigator navigator2) throws UnsupportedAxisException {
        this.navigator = navigator2;
        this.ancestorOrSelf = navigator2.getAncestorOrSelfAxisIterator(contextNode);
    }

    public boolean hasNext() {
        while (!this.childrenOrSelf.hasPrevious()) {
            try {
                if (this.stack.isEmpty()) {
                    while (!this.precedingSibling.hasNext()) {
                        if (!this.ancestorOrSelf.hasNext()) {
                            return false;
                        }
                        this.precedingSibling = new PrecedingSiblingAxisIterator(this.ancestorOrSelf.next(), this.navigator);
                    }
                    this.childrenOrSelf = childrenOrSelf(this.precedingSibling.next());
                } else {
                    this.childrenOrSelf = (ListIterator) this.stack.remove(this.stack.size() - 1);
                }
            } catch (UnsupportedAxisException e) {
                throw new JaxenRuntimeException((Throwable) e);
            }
        }
        return true;
    }

    private ListIterator childrenOrSelf(Object node) {
        try {
            ArrayList reversed = new ArrayList();
            reversed.add(node);
            Iterator childAxisIterator = this.navigator.getChildAxisIterator(node);
            if (childAxisIterator != null) {
                while (childAxisIterator.hasNext()) {
                    reversed.add(childAxisIterator.next());
                }
            }
            return reversed.listIterator(reversed.size());
        } catch (UnsupportedAxisException e) {
            throw new JaxenRuntimeException((Throwable) e);
        }
    }

    public Object next() throws NoSuchElementException {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        while (true) {
            Object result = this.childrenOrSelf.previous();
            if (!this.childrenOrSelf.hasPrevious()) {
                return result;
            }
            this.stack.add(this.childrenOrSelf);
            this.childrenOrSelf = childrenOrSelf(result);
        }
    }

    public void remove() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}
