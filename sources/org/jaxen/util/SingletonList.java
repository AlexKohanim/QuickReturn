package org.jaxen.util;

import java.util.AbstractList;

public class SingletonList extends AbstractList {
    private final Object element;

    public SingletonList(Object element2) {
        this.element = element2;
    }

    public int size() {
        return 1;
    }

    public Object get(int index) {
        if (index == 0) {
            return this.element;
        }
        throw new IndexOutOfBoundsException(index + " != 0");
    }
}
