package org.appcelerator.titanium.util;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;

public class TiWeakMap<K, V> extends HashMap<WeakReference<K>, V> {
    public boolean containsKey(Object object) {
        if (object instanceof WeakReference) {
            return super.containsKey(object);
        }
        for (WeakReference<K> ref : keySet()) {
            if (ref.get() == object) {
                return true;
            }
        }
        return false;
    }

    public V get(Object key) {
        if (key instanceof WeakReference) {
            return super.get(key);
        }
        for (WeakReference<K> ref : keySet()) {
            if (ref.get() == key) {
                return super.get(ref);
            }
        }
        return null;
    }

    public V remove(Object key) {
        if (key instanceof WeakReference) {
            return super.remove(key);
        }
        WeakReference<K> toRemove = null;
        Iterator it = keySet().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            WeakReference<K> ref = (WeakReference) it.next();
            if (ref.get() == key) {
                toRemove = ref;
                break;
            }
        }
        if (toRemove != null) {
            return super.remove(toRemove);
        }
        return null;
    }
}
