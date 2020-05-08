package org.appcelerator.titanium.util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class TiWeakList<T> extends ArrayList<WeakReference<T>> {
    protected List<WeakReference<T>> synchronizedList;

    protected class NonNullIterator implements Iterator<T> {
        protected int index;

        public NonNullIterator(int index2) {
            this.index = index2;
        }

        /* access modifiers changed from: protected */
        public int getNextIndex() {
            int size = TiWeakList.this.size();
            for (int i = this.index; i < size; i++) {
                WeakReference<T> ref = (WeakReference) TiWeakList.this.get(i);
                if (ref != null && ref.get() != null) {
                    return i;
                }
            }
            return -1;
        }

        public boolean hasNext() {
            boolean z = true;
            if (TiWeakList.this.synchronizedList != null) {
                synchronized (TiWeakList.this.synchronizedList) {
                    if (getNextIndex() < 0) {
                        z = false;
                    }
                }
                return z;
            } else if (getNextIndex() < 0) {
                return false;
            } else {
                return true;
            }
        }

        public T next() {
            T t;
            if (TiWeakList.this.synchronizedList != null) {
                synchronized (TiWeakList.this.synchronizedList) {
                    int nextIndex = getNextIndex();
                    if (nextIndex < 0) {
                        throw new NoSuchElementException();
                    }
                    this.index = nextIndex + 1;
                    t = ((WeakReference) TiWeakList.this.get(nextIndex)).get();
                }
                return t;
            }
            int nextIndex2 = getNextIndex();
            if (nextIndex2 < 0) {
                throw new NoSuchElementException();
            }
            this.index = nextIndex2 + 1;
            return ((WeakReference) TiWeakList.this.get(nextIndex2)).get();
        }

        public void remove() {
            if (TiWeakList.this.synchronizedList != null) {
                synchronized (TiWeakList.this.synchronizedList) {
                    TiWeakList.this.remove(this.index);
                }
                return;
            }
            TiWeakList.this.remove(this.index);
        }
    }

    public TiWeakList() {
        this(false);
    }

    public TiWeakList(boolean isSynchronized) {
        if (isSynchronized) {
            synchronizedList();
        }
    }

    public List<WeakReference<T>> synchronizedList() {
        if (this.synchronizedList == null) {
            this.synchronizedList = Collections.synchronizedList(this);
        }
        return this.synchronizedList;
    }

    public boolean refEquals(WeakReference<T> ref, Object o) {
        if (ref == null) {
            return false;
        }
        if (ref.get() == o) {
            return true;
        }
        if (ref.get() == null || !ref.get().equals(o)) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean findRef(Object o) {
        Iterator it = iterator();
        while (it.hasNext()) {
            if (refEquals((WeakReference) it.next(), o)) {
                return true;
            }
        }
        return false;
    }

    public boolean add(WeakReference<T> o) {
        boolean add;
        if (this.synchronizedList == null) {
            return super.add(o);
        }
        synchronized (this.synchronizedList) {
            add = super.add(o);
        }
        return add;
    }

    public boolean contains(Object o) {
        boolean findRef;
        if (o instanceof WeakReference) {
            return super.contains(o);
        }
        if (this.synchronizedList == null) {
            return findRef(o);
        }
        synchronized (this.synchronizedList) {
            findRef = findRef(o);
        }
        return findRef;
    }

    /* access modifiers changed from: protected */
    public boolean removeRef(Object o) {
        Iterator<WeakReference<T>> iter = iterator();
        while (iter.hasNext()) {
            if (refEquals((WeakReference) iter.next(), o)) {
                iter.remove();
                return true;
            }
        }
        return false;
    }

    public boolean remove(Object o) {
        boolean removeRef;
        if (o instanceof WeakReference) {
            return super.remove(o);
        }
        if (this.synchronizedList == null) {
            return removeRef(o);
        }
        synchronized (this.synchronizedList) {
            removeRef = removeRef(o);
        }
        return removeRef;
    }

    public Iterator<T> nonNullIterator() {
        return new NonNullIterator(0);
    }

    public Iterable<T> nonNull() {
        return new Iterable<T>() {
            public Iterator<T> iterator() {
                return TiWeakList.this.nonNullIterator();
            }
        };
    }
}
