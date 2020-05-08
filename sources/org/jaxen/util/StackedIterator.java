package org.jaxen.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Set;
import org.jaxen.Navigator;

public abstract class StackedIterator implements Iterator {
    private Set created = new HashSet();
    private LinkedList iteratorStack = new LinkedList();
    private Navigator navigator;

    /* access modifiers changed from: protected */
    public abstract Iterator createIterator(Object obj);

    public StackedIterator(Object contextNode, Navigator navigator2) {
        init(contextNode, navigator2);
    }

    protected StackedIterator() {
    }

    /* access modifiers changed from: protected */
    public void init(Object contextNode, Navigator navigator2) {
        this.navigator = navigator2;
    }

    /* access modifiers changed from: protected */
    public Iterator internalCreateIterator(Object contextNode) {
        if (this.created.contains(contextNode)) {
            return null;
        }
        this.created.add(contextNode);
        return createIterator(contextNode);
    }

    public boolean hasNext() {
        Iterator curIter = currentIterator();
        if (curIter == null) {
            return false;
        }
        return curIter.hasNext();
    }

    public Object next() throws NoSuchElementException {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        Object object = currentIterator().next();
        pushIterator(internalCreateIterator(object));
        return object;
    }

    public void remove() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /* access modifiers changed from: protected */
    public void pushIterator(Iterator iter) {
        if (iter != null) {
            this.iteratorStack.addFirst(iter);
        }
    }

    private Iterator currentIterator() {
        while (this.iteratorStack.size() > 0) {
            Iterator curIter = (Iterator) this.iteratorStack.getFirst();
            if (curIter.hasNext()) {
                return curIter;
            }
            this.iteratorStack.removeFirst();
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public Navigator getNavigator() {
        return this.navigator;
    }
}
