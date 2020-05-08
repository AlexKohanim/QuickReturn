package org.jaxen.javabean;

import java.util.Iterator;

public class ElementIterator implements Iterator {
    private Iterator iterator;
    private String name;
    private Element parent;

    public ElementIterator(Element parent2, String name2, Iterator iterator2) {
        this.parent = parent2;
        this.name = name2;
        this.iterator = iterator2;
    }

    public boolean hasNext() {
        return this.iterator.hasNext();
    }

    public Object next() {
        return new Element(this.parent, this.name, this.iterator.next());
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
